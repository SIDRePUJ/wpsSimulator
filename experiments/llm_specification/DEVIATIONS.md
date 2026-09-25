# Desviaciones respecto de PROTOCOL.md v1.0

## D1 — 2026-09-25 — El modo principal pasó de API a CLI

**Qué cambió.** El protocolo (sección 5) define como modo principal la generación por la
Anthropic Messages API (`--mode api`) y reserva el CLI de Claude Code (`--mode cli`) como
modo exploratorio, cuyos resultados *"are reported separately and are not part of the
confirmatory analysis"*.

Esta corrida se hizo en **modo CLI**.

**Por qué.** No hay `ANTHROPIC_API_KEY` disponible en la máquina donde se ejecuta el
experimento. Se verificó explícitamente: la variable no está definida en el entorno y el
paquete `anthropic` no está instalado. El prompt de la tarea prevé este caso e indica usar
`"mode": "cli"` registrando la desviación aquí.

**Consecuencia para el análisis.** El contraste CMH de H1 y la prueba de Wilcoxon de H2 se
calculan sobre datos del modo que el preregistro declara exploratorio. Al reportar en la
Sección 5.4 debe decirse de forma explícita que el modo preregistrado como confirmatorio no
pudo ejecutarse, y que los resultados provienen del modo exploratorio. No se debe presentar
este análisis como confirmatorio sin esa aclaración.

**Cómo revertirlo.** Con una `ANTHROPIC_API_KEY` disponible: apartar
`results/runs.jsonl` (el script saltea las corridas ya registradas comparando modelo,
condición, mecanismo y réplica, **sin mirar el modo**, de modo que las entradas de CLI
impedirían las de API), restaurar `"mode": "api"` en `config.json` y relanzar.

**Verificación previa exigida por el prompt.** Se comprobó con `claude --help` que las
opciones `-p`, `--model` y `--output-format` siguen existiendo en la versión instalada
(Claude Code 2.1.282). **No hizo falta modificar la función `call_cli` de
`run_experiment.py`**: el script quedó sin cambios.

## D2 — 2026-09-25 — `mode` en `config.json`

**Qué cambió.** El prompt de la tarea indica que el campo `models` es el único cambio
permitido a `config.json`. Además de `models`, se cambió `"mode"` de `"api"` a `"cli"`.

**Por qué.** El mismo prompt indica, para el caso sin clave, usar `"mode": "cli"`. Se optó
por dejarlo escrito en el archivo, y no solo pasarlo como argumento `--mode cli`, para que
cualquier reanudación posterior del script use el modo correcto por omisión. Una reanudación
con `python3 run_experiment.py` a secas, con `"mode": "api"` en el archivo y sin clave,
fallaría en las 200 llamadas.

**Valores escritos en `config.json`:**
- `"models": ["claude-sonnet-5", "claude-opus-5"]`
- `"mode": "cli"`

Ningún otro campo fue tocado: `mechanisms`, `conditions`, `replications`, `temperature`
(1.0), `max_tokens` (4000) y `random_seed` (20261001) conservan su valor original.

## D3 — 2026-09-25 — Rama del repositorio de congelamiento

**Qué cambió.** El prompt describe el repositorio `wpsSimulator` como *"rama `tcss-revision`,
commit liberado `2c62ef8`"*. En esta máquina el repositorio está en la rama **`main`**, con
`HEAD` en **`2c62ef8`**.

**Por qué.** El commit coincide con el liberado; solo difiere el nombre de la rama. No se
cambió de rama ni se hizo `push`, según la regla 6 del prompt. El commit de congelamiento se
hizo localmente sobre `main`.
