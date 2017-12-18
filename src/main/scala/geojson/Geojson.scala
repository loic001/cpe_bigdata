package geojson

import models.ModelResult
import models.AccessValue
import models.Area

import org.json4s.native.Serialization


case class Style(
  weight: Int = 1,
  color: String = "#999",
  opacity: Double = 1.0,
  fillColor: String = "#c0392b",
  fillOpacity: Double = 0.8
)
case class Properties(
  name: String = "defaultName",
  style: Style = Style(),
  cartodb_id: Int = -1,
  sup: Option[Double] = None
)
case class Geometry(`type`: String = "MultiPolygon", coordinates: List[List[List[List[Double]]]])
case class Feature(`type`: String = "Feature", properties: Properties, geometry: Geometry)
case class ParentFeature(`type`: String = "FeatureCollection", features: List[Feature] = List(), style: Style = Style())

object Geojson {

  implicit val formats = org.json4s.DefaultFormats

  def normalizedResult(values: List[AccessValue]) : List[AccessValue] = {
    def norm(min: Double, max: Double, value: Double) = {
      (value-min)/(max-min)
    }
    val min = values.map(_.data).reduceLeft(_ min _)
    val max = values.map(_.data).reduceLeft(_ max _)
    values.map(accV => AccessValue(norm(min, max, accV.data), accV.area))
  }

  def buildGeojsonStringFromModelResult(modelResult: ModelResult, styles: List[Style]) = {

    val ranges = for(i <- 1 to styles.size) yield i.toDouble/styles.size.toDouble

    val accessValues = normalizedResult(modelResult.data)

    val parentFeature = ParentFeature()

    val polys = accessValues.map{ value =>
      (value.area, ranges.takeWhile(range => value.data > range).size)
    }.map {
      case (area: Area, classCategory: Int) => {
        val prop = Properties(
          style=styles(classCategory),
          cartodb_id=area.name.toInt,
          sup=area.sup
        )
        val coords = area.area.get.points.map( coord => List(coord.longitude, coord.latitude))
        val geom = Geometry(coordinates=List(List(coords)))
        val feature = Feature(properties=prop, geometry=geom)
        (feature, classCategory)
      }
    }
    val classedFeatures = polys.groupBy(_._2).map{
      case (classCategory,features) => ParentFeature(features=features.map(_._1), style=styles(classCategory))
    }

    val geojson = Serialization.write(classedFeatures)
    geojson

    //val accessMap = modelResult.accessMap
  }
}
