// import org.apache.spark.SparkContext
// import org.apache.spark.SparkContext._
// import org.apache.spark.SparkConf
//

// import org.apache.spark.mllib.linalg.Matrix
// import org.apache.spark.mllib.linalg.Vectors
// import org.apache.spark.mllib.linalg.distributed.RowMatrix

import utils.GeoPoint
import models.Area
import models.SimpleScoreModel
import geojson.Geojson
import viz.LeafletRenderer
import geojson.Style

object Main {
  def main(args: Array[String]) {
    // create Spark context with Spark configuration
    //val sc = new SparkContext(new SparkConf().setAppName("Spark Count").setMaster("local[*]"))

    val PATH = "data/"
    val ESCALIER = PATH + "escaliers.csv"
    val ACCESSIBILITE = PATH + "a11y.csv"
    val PARKING = PATH + "parking.csv"

    val latStartParis: Double = 48.906796
    val lngStartParis: Double = 2.2377780
    val latEndParis: Double = 48.815545
    val lngEndParis:Double = 2.4396510

    val startPoint = GeoPoint(latStartParis, lngStartParis)
    val endPoint = GeoPoint(latEndParis, lngEndParis)

    val areas = Area.buildAreas(startPoint, endPoint, blocks=15)

    //init data
    areas.foreach(area => area.initSources(0,0,0))

    //or fake data
    //areas.foreach(area => area.randomSources)


    //spark get sources
    readFile(ESCALIER).foreach(row => {
      val longitude: Double = row(1).toDouble
      val latitude: Double = row(2).toDouble

      Area.getAreaByLatLng(areas, GeoPoint(latitude, longitude)).map(_.addStairs())
    })


    readFile(PARKING).foreach(row => {
      val longitude: Double = row(0).toDouble
      val latitude: Double = row(1).toDouble
      val nb: Int = row(2).toInt

      Area.getAreaByLatLng(areas, GeoPoint(latitude, longitude)).map(_.addParking(nb))
    })

    readFile(ACCESSIBILITE).foreach(row => {
      val longitude: Double = row(1).toDouble
       val latitude: Double = row(2).toDouble
       val accessType: String = row(4)
       val level: Int = row(3).toInt

      Area.getAreaByLatLng(areas, GeoPoint(latitude, longitude)).map(_.addAccessibility(accessType,level))
    })


    //fit model
    val simpleModel: SimpleScoreModel = new SimpleScoreModel()
    val modelResult = simpleModel.fit(areas)

    //build geojson
    val color = "#32CD32"
    val styles = List(0.3, 0.5, 0.7).map(opacity => Style(fillColor=color, fillOpacity=opacity))
    val geojsonString = Geojson.buildGeojsonStringFromModelResult(modelResult, styles)

    //render and save plot html file
    LeafletRenderer.render(geojsonString, "out.html")
  }


  def readFile(path : String) : Array[Array[String]] = {
    val bufferedSource = scala.io.Source.fromFile(path)
    var content = scala.collection.mutable.ArrayBuffer.empty[Array[String]]
    for (line <- bufferedSource.getLines) {
      val cols = line.split(",").map(_.trim)
      content += cols
    }
    bufferedSource.close
    content.toArray
  }


}
