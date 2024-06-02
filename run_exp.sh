#!/bin/bash

# Asegurarse de que se está ejecutando con Bash
if [ -z "$BASH_VERSION" ]; then
    echo "Este script debe ser ejecutado con bash"
    exit 1
fi

java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 2 --personality 0.5 --tools 10 --seeds 10 --water 0 --irrigation 0
mv logs/ E401/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 2 --personality 0.5 --tools 10 --seeds 999999 --water 999999 --irrigation 1
mv logs/ E402/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 2 --personality 0.5 --tools 10 --seeds 0 --water 0 --irrigation 1
mv logs/ E403/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 6 --personality 0.0 --tools 999999 --seeds 10 --water 0 --irrigation 0
mv logs/ E404/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 6 --personality 0.0 --tools 999999 --seeds 999999 --water 999999 --irrigation 1
mv logs/ E405/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 6 --personality 0.0 --tools 999999 --seeds 0 --water 0 --irrigation 1
mv logs/ E406/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 12 --personality -0.5 --tools 0 --seeds 10 --water 0 --irrigation 0
mv logs/ E407/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 12 --personality -0.5 --tools 0 --seeds 999999 --water 999999 --irrigation 1
mv logs/ E408/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 750000 --land 12 --personality -0.5 --tools 0 --seeds 0 --water 0 --irrigation 1
mv logs/ E409/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 2 --personality 0.0 --tools 0 --seeds 10 --water 999999 --irrigation 1
mv logs/ E410/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 2 --personality 0.0 --tools 0 --seeds 999999 --water 0 --irrigation 1
mv logs/ E411/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 2 --personality 0.0 --tools 0 --seeds 0 --water 0 --irrigation 0
mv logs/ E412/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 6 --personality -0.5 --tools 10 --seeds 10 --water 999999 --irrigation 1
mv logs/ E413/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 6 --personality -0.5 --tools 10 --seeds 999999 --water 0 --irrigation 1
mv logs/ E414/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 6 --personality -0.5 --tools 10 --seeds 0 --water 0 --irrigation 0
mv logs/ E415/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 12 --personality 0.5 --tools 999999 --seeds 10 --water 999999 --irrigation 1
mv logs/ E416/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 12 --personality 0.5 --tools 999999 --seeds 999999 --water 0 --irrigation 1
mv logs/ E417/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 1500000 --land 12 --personality 0.5 --tools 999999 --seeds 0 --water 0 --irrigation 0
mv logs/ E418/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 2 --personality -0.5 --tools 999999 --seeds 10 --water 0 --irrigation 1
mv logs/ E419/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 2 --personality -0.5 --tools 999999 --seeds 999999 --water 0 --irrigation 0
mv logs/ E420/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 2 --personality -0.5 --tools 999999 --seeds 0 --water 999999 --irrigation 1
mv logs/ E421/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 6 --personality 0.5 --tools 0 --seeds 10 --water 0 --irrigation 1
mv logs/ E422/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 6 --personality 0.5 --tools 0 --seeds 999999 --water 0 --irrigation 0
mv logs/ E423/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 6 --personality 0.5 --tools 0 --seeds 0 --water 999999 --irrigation 1
mv logs/ E424/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 12 --personality 0.0 --tools 10 --seeds 10 --water 0 --irrigation 1
mv logs/ E425/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 12 --personality 0.0 --tools 10 --seeds 999999 --water 0 --irrigation 0
mv logs/ E426/
java -jar wpsim-1.0.jar --mode single --agents 100 --money 3000000 --land 12 --personality 0.0 --tools 10 --seeds 0 --water 999999 --irrigation 1
mv logs/ E427/