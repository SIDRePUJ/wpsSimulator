cd out/artifacts/wpsim_jar
QQ=5
for i in $(seq 1 $QQ); do
    echo "==================================================="
    echo "                 $i of $QQ                         "
    echo "==================================================="
    java -jar wpsim.jar single
done
