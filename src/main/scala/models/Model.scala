package models

import models.Area

object Model {
  // def plotModelResult(modelResult: ModelResult) = {
  //   val geojson = Geojson.buildGeojsonStringFromAccessMap(modelResult.accessMap)
  //   val outHtml = LeafletRender.render(geojson)
  // }
}

abstract class Model {
  def fit(areas: List[Area]) : ModelResult

}
