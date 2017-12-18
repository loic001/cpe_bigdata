import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark._

object SparkWordCount {
def main(args: Array[String]) {
  val sc = new SparkContext(new SparkConf().setAppName("wordcount"))

/* local = master URL; Word Count = application name; */
/* /usr/local/spark = Spark Home; Nil = jars; Map = environment */
/* Map = variables to work nodes */
/*creating an inputRDD to read text file (in.txt) through Spark context*/
val inputfile = sc.textFile("file:///home/lolo/projects/cpe/bigdata/cpe_bigdata/data/html/leaflet01.mustache")


/* Transform the inputRDD into countRDD */
 val counts = inputfile.flatMap(line => line.split(" ")).map(word =>
 (word, 1)).reduceByKey(_+_);
/* saveAsTextFile method is an action that effects on the RDD */

print(inputfile)



}
}
