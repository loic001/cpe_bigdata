package models

import utils.Polygon
import utils.GeoPoint
import sources._
import utils.PolygonUtils

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.io.Source

import geojson._

object Area{

implicit val formats = DefaultFormats

  def buildFromGeojson(filename: String) : List[Area] = {

    val o = Array(Array(Array(1,2,3)))
    o(0)(0)

    val fileContents = Source.fromFile(filename).getLines.mkString

    val parent: ParentFeature = parse(fileContents).extract[ParentFeature]

    val areas = parent.features.map{feature =>
      val polys = feature.geometry.coordinates(0)(0).map(coord => GeoPoint(coord(1), coord(0)))
      Area(-1, -1,feature.properties.cartodb_id.toString,Some(Polygon(polys)), feature.properties.sup)

    }
    areas
  }

  def buildAreas(start: GeoPoint, end: GeoPoint, blocks: Int = 20) : List[Area] = {
    //val factor = 1000000.0

    val blockLat: Double = Math.abs((start.latitude - end.latitude)/blocks.toDouble)
    val blockLong: Double = Math.abs((start.longitude - end.longitude)/blocks.toDouble)

    val areas = for(i <- 0 to blocks ; j <- 0 to blocks) yield {
      val a = GeoPoint(start.latitude - (i*blockLat), start.longitude + (j*blockLong))
      val b = GeoPoint(a.latitude, a.longitude + blockLong)
      val c = GeoPoint(a.latitude - blockLat, a.longitude + blockLong)
      val d = GeoPoint(a.latitude - blockLat, a.longitude)

      val poly = Polygon(List(a,b,c,d))

      Area(x=i, y=j, area=Some(poly))
    }
    areas.toList
  }

    def getAreaByLatLng(areas: List[Area], point: GeoPoint) = areas.filter(area => PolygonUtils.pointInPolygon(point, area.area.getOrElse(Polygon(List())))).headOption
    def getArea(areas: List[Area], x: Int, y: Int) = areas.filter(area => area.x==x && area.y==y).head

}

case class Area(
  var x: Int,
  var y: Int,
  var name: String = "default name",
  var area: Option[Polygon] = None,
  var sup: Option[Double] = None) extends Stairs with Parking with Accessibility{


    val r = scala.util.Random

    def initSources(stairsCount: Int, accessibility: Int, parkingCount: Int) = {
      this.stairsCount = stairsCount
      this.accessibility = accessibility
      this.parkingCount = parkingCount
      this
    }

    def randomSources() = this.initSources(r.nextInt(50), r.nextInt(100), r.nextInt(5))

  }
