package models

import models.Area

//naive model
class StairsOnly extends Model {

  def scoreCalculation(stairs: Int, acc: Int, park: Int) : Int = {
    var score = 0


    score+=(stairs/10).toInt

    score
  }

  def fit(areas: List[Area]) : ModelResult = {
    val values = areas.map { area =>
      AccessValue(scoreCalculation(area.stairsCount, area.accessibility, area.parkingCount)/area.sup.getOrElse(1.0), area)
    }

    ModelResult(data=values)
  }
}
