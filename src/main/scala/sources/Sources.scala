package sources


trait Stairs {
  var stairsCount: Int = 0

  def addStairs() = {
    stairsCount += 1
  }
}

trait Accessibility {
  var accessibility: Int = 0

  val accessMap = Map(
    "Bains douches" -> 1,
    "Bibliothèque" -> 1,
    "CIO" -> 1,
    "Cimetière" -> 1,
    "Collèges" -> 1,
    "Conservatoire" -> 1,
    "Crèches" -> 1,
    "Ecoles maternelles" -> 1,
    "Ecoles élémentaires" -> 1,
    "Equipement Jeunes" -> 1,
    "Equipement sportif" -> 1,
    "Halte-garderie" -> 1,
    "Jardins" -> 1,
    "Lycées" -> 1,
    "Mairies" -> 1,
    "Musée" -> 1,
    "Piscine" -> 1
  )
  def convert(str: String, accessMap: Map[String, Int] = accessMap): Int = {
    if (accessMap isDefinedAt str) accessMap(str) else 1
  }

  def addAccessibility(poiType: String, level: Int) {
     accessibility += convert(poiType) * level
   }
}

trait Parking {
  var parkingCount: Int = 0

  def addParking (nb : Int) = {
    parkingCount += nb
  }
}
