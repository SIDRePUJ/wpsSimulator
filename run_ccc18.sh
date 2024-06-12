#!/bin/bash

# Asegurarse de que se está ejecutando con Bash
if [ -z "$BASH_VERSION" ]; then
    echo "Este script debe ser ejecutado con bash"
    exit 1
fi

java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC01/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC02/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC03/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC04/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC05/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC06/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC07/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC08/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC09/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC10/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC11/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC12/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC13/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 2 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC14/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC15/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 6 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC16/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 1
mv logs/ CCC17/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 12 --personality 0.0 --tools 20 --seeds 50 --water 0 --irrigation 0 --emotions 0
mv logs/ CCC18/