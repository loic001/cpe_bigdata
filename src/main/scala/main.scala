import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
//

// import org.apache.spark.mllib.linalg.Matrix
// import org.apache.spark.mllib.linalg.Vectors
// import org.apache.spark.mllib.linalg.distributed.RowMatrix

import utils.GeoPoint
import models._
import geojson.{Geojson,Style}
import viz.LeafletRenderer

import sources._

import org.json4s.native.Serialization
import org.json4s.FieldSerializer
import org.json4s.DefaultFormats

import scala.reflect.runtime.universe

object Main {

  implicit val formats = DefaultFormats + FieldSerializer[Area with Stairs with Parking with Accessibility]()

  def main(args: Array[String]) {
    //create Spark context with Spark configuration
  //  val sc = new SparkContext(new SparkConf().setAppName("bigdata_2").setMaster("spark://127.0.0.1:7077"))
    //val conf = new SparkConf().setAppName("bigdata")
    //val sc = new SparkContext(conf)

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

    //val areas = Area.buildAreas(startPoint, endPoint, blocks=15)

    val areas = Area.buildFromGeojson("data/geojson/paris.json")


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


    // val areasJson = Serialization.write(areas)
    // print(areasJson)
    //
    //
    // import java.nio.file.{Paths, Files}
    // import java.nio.charset.StandardCharsets
    //
    // Files.write(Paths.get("areas.json"), areasJson.getBytes(StandardCharsets.UTF_8))
  //  fit model

    val models = Map(
      "SimpleScoreModelNoStairs" -> new SimpleScoreModelNoStairs(),
      "StairsOnly" -> new StairsOnly(),
      "SimpleScoreModel" -> new SimpleScoreModel()
    )

    models.map {
      case (name: String, model: Model) => {
        val modelResult = model.fit(areas)
        val color = "#32CD32"
        val styles = List(0.3, 0.5, 0.7).map(opacity => Style(fillColor=color, fillOpacity=opacity))
        val geojsonString = Geojson.buildGeojsonStringFromModelResult(modelResult, styles)
        LeafletRenderer.render(geojsonString, s"model_out/$name.html")
      }
    }


    //sc.stop()
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
