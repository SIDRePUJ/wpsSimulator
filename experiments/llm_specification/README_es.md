# Mini-experimento: especificación en prosa vs. fila de la matriz (LLM)

Protocolo completo (en inglés, para el suplemento y el repositorio): `PROTOCOL.md`.

## Antes de correr (congelar)

1. Poner los identificadores exactos de los modelos Sonnet y Opus en `config.json`
   (campo `models`).
2. Revisar las especificaciones (`specs/`), las pruebas ocultas (`tests_hidden/`) y el
   prompt (`prompt_template.txt`). Después de este paso no se cambian.
3. Generar los hashes: `sha256sum specs/* stubs/* tests_hidden/* reference/* prompt_template.txt config.json PROTOCOL.md > MANIFEST.sha256`
   y hacer commit en el repositorio con fecha (sirve como preregistro).

## Correr

```bash
pip install anthropic pytest scipy statsmodels numpy
export ANTHROPIC_API_KEY=...
python3 run_experiment.py --mode api      # 200 generaciones; se puede reanudar
python3 evaluate.py                       # pruebas ocultas -> results/evaluation.csv
python3 analyze.py                        # tablas y pruebas -> results/summary.md
```

Opcional (exploratorio, se reporta aparte): `python3 run_experiment.py --mode cli`
(Claude Code CLI; verificar antes las opciones con `claude --help`).

## Después

- Clasificar los fallos (`failed_tests` en `evaluation.csv`) con las categorías del
  protocolo, sección 6.
- Enviar `results/summary.md` para completar la Sección 5.4 del manuscrito.
- Cualquier desviación va en `DEVIATIONS.md`.

## Validación ya hecha

- Las implementaciones de referencia pasan todas las pruebas ocultas (46 pruebas).
- Seis mutantes que reproducen los tipos de defecto históricos fallan al menos una prueba.
- La cadena evaluate/analyze se probó con datos sintéticos que luego se borraron; `results/`
  solo contiene los prompts de la corrida en seco.
