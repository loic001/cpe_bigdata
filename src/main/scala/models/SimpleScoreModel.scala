package models

import models.Area
import models.AccessValue

//naive model
class SimpleScoreModel extends Model {

  def scoreCalculation(stairs: Int, acc: Int, park: Int) : Int = {
    var score = 0
    if(stairs < 3){
      score+=1
    }else if(stairs < 5){
      score+=2
    }else if(stairs < 10){
      score+=3
    }else{
      score+=5
    }

    if(acc < 3){
      score+=1
    }else if(acc < 5){
      score+=2
    }else if(acc < 10){
      score+=3
    }else{
      score+=5
    }

    if(park < 3){
      score+=1
    }else if(park < 5){
      score+=2
    }else if(park < 10){
      score+=3
    }else{
      score+=5
    }
    score
  }

  def fit(areas: List[Area]) : ModelResult = {
    val values = areas.map { area =>
      AccessValue(scoreCalculation(area.stairsCount, area.accessibility, area.parkingCount), area)
    }

    ModelResult(data=values)
  }
}
