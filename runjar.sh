cd out/artifacts/wpsim_jar
for i in $(seq $1 $2); do
    echo "==================================================="
    echo "                 $i of $2                         "
    echo "==================================================="
    java -jar wpsim.jar single
    mv logs/wpsSimulator.csv logs/wpsSimulator-$i.csv
    mv logs/wpsSimulator.log logs/wpsSimulator-$i.log
done
