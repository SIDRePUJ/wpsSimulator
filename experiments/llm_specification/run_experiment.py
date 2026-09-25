#!/usr/bin/env python3
"""Generate implementations of each mechanism from each specification condition.

Usage:
    python3 run_experiment.py --mode api   # Anthropic Messages API (ANTHROPIC_API_KEY required)
    python3 run_experiment.py --mode cli   # Claude Code CLI in non-interactive mode (`claude -p`)
    python3 run_experiment.py --dry-run    # write the prompts only, no model calls

Every call is logged in results/runs.jsonl (model, condition, mechanism, replication,
prompt hash, raw response, extracted code). Runs already present in the log are skipped,
so the script can be resumed after an interruption.
"""
import argparse, hashlib, json, os, random, re, subprocess, sys, tempfile, time
from datetime import datetime, timezone

HERE = os.path.dirname(os.path.abspath(__file__))
CFG = json.load(open(os.path.join(HERE, "config.json")))
LOG = os.path.join(HERE, "results", "runs.jsonl")


def sha(text):
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def build_prompt(mech, cond):
    module = CFG["mechanisms"][mech]
    spec = open(os.path.join(HERE, "specs", f"{mech}_{cond}.md"), encoding="utf-8").read()
    stub = open(os.path.join(HERE, "stubs", f"{module}.py"), encoding="utf-8").read()
    tmpl = open(os.path.join(HERE, "prompt_template.txt"), encoding="utf-8").read()
    return tmpl.format(module=module, spec=spec.strip(), stub=stub.strip())


def extract_code(text):
    blocks = re.findall(r"```(?:python)?\s*\n(.*?)```", text, re.S)
    return max(blocks, key=len) if blocks else text


def call_api(model, prompt):
    import anthropic  # pip install anthropic
    client = anthropic.Anthropic()
    msg = client.messages.create(model=model, max_tokens=CFG["max_tokens"],
                                 temperature=CFG["temperature"],
                                 messages=[{"role": "user", "content": prompt}])
    return "".join(b.text for b in msg.content if getattr(b, "type", "") == "text")


def call_cli(model, prompt):
    # Runs in an empty temporary directory so that the CLI cannot read the tests,
    # the reference implementations, or the simulator repository.
    # Verify the flags with `claude --help` for the installed version before running.
    with tempfile.TemporaryDirectory() as d:
        out = subprocess.run(["claude", "-p", prompt, "--model", model, "--output-format", "text"],
                             cwd=d, capture_output=True, text=True, timeout=600)
    if out.returncode != 0:
        raise RuntimeError(out.stderr[-2000:])
    return out.stdout


def done_keys():
    keys = set()
    if os.path.exists(LOG):
        for line in open(LOG, encoding="utf-8"):
            r = json.loads(line)
            if r.get("ok"):
                keys.add((r["model"], r["condition"], r["mechanism"], r["replication"]))
    return keys


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--mode", choices=["api", "cli"], default=CFG["mode"])
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    os.makedirs(os.path.join(HERE, "results"), exist_ok=True)

    plan = [(m, c, k, r) for m in CFG["models"] for c in CFG["conditions"]
            for k in CFG["mechanisms"] for r in range(1, CFG["replications"] + 1)]
    random.Random(CFG["random_seed"]).shuffle(plan)  # randomized run order

    if args.dry_run:
        pdir = os.path.join(HERE, "results", "prompts")
        os.makedirs(pdir, exist_ok=True)
        for k in CFG["mechanisms"]:
            for c in CFG["conditions"]:
                open(os.path.join(pdir, f"{k}_{c}.txt"), "w", encoding="utf-8").write(build_prompt(k, c))
        print(f"{len(plan)} planned runs; prompts written to {pdir}")
        return

    cli_version = ""
    if args.mode == "cli":
        cli_version = subprocess.run(["claude", "--version"], capture_output=True, text=True).stdout.strip()

    skip = done_keys()
    with open(LOG, "a", encoding="utf-8") as log:
        for model, cond, mech, rep in plan:
            if (model, cond, mech, rep) in skip:
                continue
            prompt = build_prompt(mech, cond)
            rec = {"model": model, "condition": cond, "mechanism": mech, "replication": rep,
                   "mode": args.mode, "cli_version": cli_version, "prompt_sha256": sha(prompt),
                   "started": datetime.now(timezone.utc).isoformat()}
            try:
                raw = call_api(model, prompt) if args.mode == "api" else call_cli(model, prompt)
                code = extract_code(raw)
                rec.update(ok=True, raw=raw, code=code, code_sha256=sha(code))
            except Exception as e:  # API or CLI failure: logged and retried on the next invocation
                rec.update(ok=False, error=repr(e))
            rec["finished"] = datetime.now(timezone.utc).isoformat()
            log.write(json.dumps(rec, ensure_ascii=False) + "\n")
            log.flush()
            print(model, cond, mech, rep, "ok" if rec["ok"] else "ERROR")
            time.sleep(1)


if __name__ == "__main__":
    main()
