package models

import models.Area

//naive model
class SimpleScoreModelNoStairs extends Model {

  def scoreCalculation(stairs: Int, acc: Int, park: Int) : Int = {
    var score = 0
    // if(stairs < 30){
    //   score-=1
    // }else if(stairs < 70){
    //   score-=2
    // }else if(stairs < 100){
    //   score-=3
    // }else{
    //   score-=5
    // }

    // if(acc < 50){
    //   score+=1
    // }else if(acc < 100){
    //   score+=2
    // }else if(acc < 200){
    //   score+=3
    // }else{
    //   score+=4
    // }

    //score-=(stairs/10).toInt

    score+=(acc/30).toInt

    score+=(park/70).toInt
    score
  }

  def fit(areas: List[Area]) : ModelResult = {
    val values = areas.map { area =>
      AccessValue(scoreCalculation(area.stairsCount, area.accessibility, area.parkingCount)/area.sup.getOrElse(1.0), area)
    }

    ModelResult(data=values)
  }
}
