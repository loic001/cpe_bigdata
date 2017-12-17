sbt package

$SPARK_HOME/sbin/start-master.sh --webui-port 6767 -h 127.0.0.1
$SPARK_HOME/sbin/start-slave.sh spark://127.0.0.1:7077

$SPARK_HOME/bin/spark-submit --master spark://LoloLinux01:7077 --class Main <path to jar/cpe_bigdata_2.11-1.0.jar> excalier.csv
