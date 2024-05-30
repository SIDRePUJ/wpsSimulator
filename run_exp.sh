#!/bin/bash

# Asegurarse de que se está ejecutando con Bash
if [ -z "$BASH_VERSION" ]; then
    echo "Este script debe ser ejecutado con bash"
    exit 1
fi

cd /home/ubuntu/wpsim/wpsSimulator/out/artifacts/wpsim_jar

# Nombre del archivo de salida del simulador
OUTPUT_FILE="salida_simulador.log"

# Comando para ejecutar el simulador y redirigir la salida a un archivo y a la pantalla
java -Xms48g -Xmx48g -jar wpsim.jar single 100 > >(tee $OUTPUT_FILE) 2>&1 &

# Obtener el PID del proceso del simulador
SIMULATOR_PID=$!

# Patrón de error a buscar
ERROR_PATTERN="Couldn't send the event because the agent state is \"kill\""

# Variable para contar las ocurrencias del error
ERROR_COUNT=0
ERROR_THRESHOLD=20

# Función para monitorear el archivo de salida
monitor_output() {
    tail -F $OUTPUT_FILE | while read LINE; do
        case "$LINE" in
            *"$ERROR_PATTERN"*)
                ERROR_COUNT=$((ERROR_COUNT + 1))
                echo "Error detectado: $LINE (Conteo: $ERROR_COUNT)"
                if [ "$ERROR_COUNT" -ge "$ERROR_THRESHOLD" ]; then
                    echo "Error detectado $ERROR_THRESHOLD veces. Terminando el proceso $SIMULATOR_PID."
                    kill "$SIMULATOR_PID"
                    exit 0
                fi
                ;;
        esac
    done
}

# Ejecutar la función de monitoreo en segundo plano
monitor_output &
MONITOR_PID=$!

# Esperar a que el proceso del simulador termine
wait $SIMULATOR_PID

# Terminar el proceso de monitoreo
kill $MONITOR_PID
