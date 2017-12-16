package const

import org.json4s.native.Serialization

object Gen extends App {
  //Page 1
  /**
   * escaliers => [ id, longitude, latitude]
   * accessibility => [ id, longitude, latitude, accessibilite, type]
   *     type :
   *         Bains douches
   *         Bibliothèque
   *         CIO
   *         Cimetière
   *         Collèges
   *         Conservatoire
   *         Crèches
   *         Ecoles maternelles
   *         Ecoles élémentaires
   *         Equipement Jeunes
   *         Equipement sportif
   *         Halte-garderie
   *         Jardins
   *         Lycées
   *         Mairies
   *         Musée
   *         Piscine
   *      accessiblite :
   *         0 : Non accessible
   * 1 : Accessibilité minimale : L’accueil du public est accessible pour tous
   * 2 : Accessibilité d'usage : Tous les services au public sont accessibles pour tous
   * 3 : Accessibilité totale : Tous les locaux ouverts au public sont accessibles pour tous
   * 4 : Accessibilité totale ERP + accessibilité locaux de travail : niveau 3 ERP + tous les locaux de travail sont accessibles pour le personnel
   * -1 : Donnée non renseignée
   *
   * parking => [id, longitude, latitude, nb]
   * liste_arrets => [ agency_name	string
   * route_id	string
   * route_short_name	string
   * route_long_name	string
   * route_type	tinyint
   * stop_id	string
   * stop_name	string
   * latitude	double
   * longitude	double
   * zder_id_ref_a	string
   * id_line	string
   * pointgeo	string ]
   *
   * route_id = mle_externalcode
   *
   * source_stif_accessible => [ mle_externalcode	string
   * mle_acces_ufr	tinyint
   * mle_acces_plancher	tinyint
   * mle_acces_rampe	tinyint
   * mle_son	tinyint
   * mle_visu tinyint ]
   */

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

  class Area(var stairsCount: Int, var accessibility: Int, var parking: Int) {
    //def this() = this(r.nextInt(50), r.nextInt(100), r.nextInt(5))
    def addStairs() {
      stairsCount += 1
    }
    def addParking(nb: Int) = {
      parking += nb
    }
    def addAccessibility(poiType: String, level: Int) {
      accessibility += convert(poiType) * level
    }
    def scoreCalculation: Double = {
      stairsCount / 50.0
    }
    override def toString: String = {
      "(" + stairsCount + ", " + accessibility + ", " + parking + ")"
    }
  }

  object Area {
    val r = scala.util.Random
    def buildFakeArea() = new Area(r.nextInt(50), r.nextInt(100), r.nextInt(5))
    def buildFakeAreaMap(row: Int, col: Int) = {
      val areas = Array.ofDim[Area](row, col)
      for {
        row <- 0 to MAX_ROW - 1
        col <- 0 to MAX_ROW - 1
      } areas(row)(col) = Area.buildFakeArea()
      areas
    }

  }

  //Page 2
  //Notes coordonées GPS pour Paris
  //Latitude : 48.906796 | Longitude : 2.237778 -- Haut gauche
  //Latitude : 48.815545 | Longitude : 2.439651 -- Bas droite
  val MAX_ROW = 20

  val MULTI = 1000000.0

  val lat_x = 48906796
  val lng_x = 2237778
  val lat_y = 48815545
  val lng_y = 2439651
  //En bas à droite
  val diffLat = (lat_x - lat_y) / MAX_ROW
  val diffLng = (lng_y - lng_x) / MAX_ROW

  //Test latitude : Latitude : 48.834532 | Longitude : 2.353134
  val lat_test = 48.834532
  val lng_test = 2.353134

  var lat_test_act = lat_test * MULTI
  var lng_test_act = lng_test * MULTI

  //Init table
  // val mapScore = Array.ofDim[Area](MAX_ROW, MAX_ROW)
  // for( row <- 0 to MAX_ROW - 1) {
  //   for( col <- 0 to MAX_ROW - 1) {
  //     mapScore(row)(col) = Area.buildFakeArea()
  //   }
  // }
  val mapScore = Area.buildFakeAreaMap(MAX_ROW, MAX_ROW)

  implicit val formats = org.json4s.DefaultFormats
  val json = Serialization.write(mapScore)

  //  import org.apache.spark.sql.hive._
  //  import spark.implicits._
  //  import org.apache.spark.sql.Row
  //  val hc = new HiveContext(sc)

  //PAGE 6
  //println(mapScore.deep.mkString("\n"))

  //Page 7
  val beginPoint = """{"type":"Feature","properties":{"name":"Test","x":1,"y":1,"cartodb_id":2,"created_at":"2013-02-26T07:07:16.384Z","updated_at":"2013-02-26T18:36:18.682Z"},"geometry":{"type":"MultiPolygon","coordinates":[[["""
  val endPoint = """]]]}},"""

  //Page 8
  var geoJson1, geoJson2, geoJson3, geoJson4, geoJson5, geoJson6, geoJson7, geoJson8 = "["
  var x = 0;
  var y = 0;
  // A ----- B
  // |       |
  // D ----- C
  var pointA = "";
  var pointB = "";
  var pointC = "";
  var pointD = "";

  var tmp = "";
  var scoreResult = 0.0;

  for (x <- 0 to MAX_ROW - 1) {
    for (y <- 0 to MAX_ROW - 1) {
      tmp = beginPoint
      pointA = "[" + (lng_x + diffLng * x) / MULTI + "," + (lat_y + diffLat * y) / MULTI + "],"
      pointB = "[" + (lng_x + diffLng * (x + 1)) / MULTI + "," + (lat_y + diffLat * y) / MULTI + "],"
      pointC = "[" + (lng_x + diffLng * (x + 1)) / MULTI + "," + (lat_y + diffLat * (y + 1)) / MULTI + "],"
      pointD = "[" + (lng_x + diffLng * x) / MULTI + "," + (lat_y + diffLat * (y + 1)) / MULTI + "]"
      tmp += pointA + pointB + pointC + pointD + endPoint
      scoreResult = mapScore(x)(y).scoreCalculation
      if (scoreResult > 0.875) {
        geoJson1 += tmp;
      } else if (scoreResult > 0.750) {
        geoJson2 += tmp;
      } else if (scoreResult > 0.625) {
        geoJson3 += tmp;
      } else if (scoreResult > 0.5) {
        geoJson4 += tmp;
      } else if (scoreResult > 0.375) {
        geoJson5 += tmp;
      } else if (scoreResult > 0.250) {
        geoJson6 += tmp;
      } else if (scoreResult > 0.125) {
        geoJson7 += tmp;
      } else if (scoreResult > 0) {
        geoJson8 += tmp;
      }
    }
  }
  geoJson1 += "]"
  geoJson2 += "]"
  geoJson3 += "]"
  geoJson4 += "]"
  geoJson5 += "]"
  geoJson6 += "]"
  geoJson7 += "]"
  geoJson8 += "]"



  val out = s"""
  <!DOCTYPE html>
  <html>
  <head>
  	<title>GeoJSON tutorial - Leaflet</title>
  	<meta charset="utf-8" />
  	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  	<link rel="shortcut icon" type="image/x-icon" href="docs/images/favicon.ico" />
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css" integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ==" crossorigin=""/>
      <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js" integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log==" crossorigin=""></script>
  	<style>
  		html, body { height: 100%;margin: 0;}
  		#map { width: 600px; height: 400px;}
  	</style>
  </head>
  <body>
  <div id='map'></div>
  <script type="text/javascript">
  	var map = L.map('map').setView([48.87, 2.30], 11);

      /* COLORS */
      //Red
      var color1 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#c0392b",fillOpacity: 0.8};
      var color2 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#e74c3c",fillOpacity: 0.8};
      var color3 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#d35400",fillOpacity: 0.8};
      var color4 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#e67e22",fillOpacity: 0.8};
      var color5 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#f39c12",fillOpacity: 0.8};
      var color6 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#f1c40f",fillOpacity: 0.8};
      var color7 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#1abc9c",fillOpacity: 0.8};
      var color8 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#27ae60",fillOpacity: 0.8};
      //Green

  	var set1 = {"type":"FeatureCollection","features": []};
      var set2 = {"type":"FeatureCollection","features": []};
      var set3 = {"type":"FeatureCollection","features": []};
      var set4 = {"type":"FeatureCollection","features": []};
      var set5 = {"type":"FeatureCollection","features": []};
      var set6 = {"type":"FeatureCollection","features": []};
      var set7 = {"type":"FeatureCollection","features": []};
      var set8 = {"type":"FeatureCollection","features": []};

      set1.features = $geoJson1;
      set2.features = $geoJson2;
      set3.features = $geoJson3;
      set4.features = $geoJson4;
      set5.features = $geoJson5;
      set6.features = $geoJson6;
      set7.features = $geoJson7;
      set8.features = $geoJson8;

  	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
  		maxZoom: 18,
  		attribution: 'CPE - for IRC Students - Stand up for Paris',
  		id: 'mapbox.light'
  	}).addTo(map);
      function onEachFeature(feature, layer) {
          var popupContent = "<p>I started out as a GeoJSON " +
              feature.geometry.type + ", but now I'm a Leaflet vector!</p>";
          if (feature.properties && feature.properties.popupContent) {
              popupContent += feature.properties.popupContent;
          }
          layer.bindPopup(popupContent);
      }
  	L.geoJSON( set1, {
  		style: function (feature) {
              return color1
  		},
  	}).addTo(map);
      L.geoJSON( set2, {
  		style: function (feature) {
              return color2
  		},
  	}).addTo(map);
      L.geoJSON( set3, {
  		style: function (feature) {
              return color3
  		},
  	}).addTo(map);
      L.geoJSON( set4, {
  		style: function (feature) {
              return color4
  		},
  	}).addTo(map);
      L.geoJSON( set5, {
  		style: function (feature) {
              return color5
  		},
  	}).addTo(map);
      L.geoJSON( set6, {
  		style: function (feature) {
              return color6
  		},
  	}).addTo(map);
      L.geoJSON( set7, {
  		style: function (feature) {
              return color7
  		},
  	}).addTo(map);
      L.geoJSON( set8, {
  		style: function (feature) {
              return color8
  		},
  	}).addTo(map);
  </script>
  </body>
  </html>
  """


  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets

  Files.write(Paths.get("filename.html"), out.getBytes(StandardCharsets.UTF_8))
  
  //Page 9
  //   print(s"""
  // <!DOCTYPE html>
  // <html>
  // <head>
  // 	<title>GeoJSON tutorial - Leaflet</title>
  // 	<meta charset="utf-8" />
  // 	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  // 	<link rel="shortcut icon" type="image/x-icon" href="docs/images/favicon.ico" />
  //     <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css" integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ==" crossorigin=""/>
  //     <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js" integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log==" crossorigin=""></script>
  // 	<style>
  // 		html, body { height: 100%;margin: 0;}
  // 		#map { width: 600px; height: 400px;}
  // 	</style>
  // </head>
  // <body>
  // <div id='map'></div>
  // <script type="text/javascript">
  // 	var map = L.map('map').setView([48.87, 2.30], 11);
  //
  //     /* COLORS */
  //     //Red
  //     var color1 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#c0392b",fillOpacity: 0.8};
  //     var color2 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#e74c3c",fillOpacity: 0.8};
  //     var color3 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#d35400",fillOpacity: 0.8};
  //     var color4 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#e67e22",fillOpacity: 0.8};
  //     var color5 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#f39c12",fillOpacity: 0.8};
  //     var color6 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#f1c40f",fillOpacity: 0.8};
  //     var color7 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#1abc9c",fillOpacity: 0.8};
  //     var color8 = {weight: 1, color: "#999", opacity: 0.2, fillColor: "#27ae60",fillOpacity: 0.8};
  //     //Green
  //
  // 	var set1 = {"type":"FeatureCollection","features": []};
  //     var set2 = {"type":"FeatureCollection","features": []};
  //     var set3 = {"type":"FeatureCollection","features": []};
  //     var set4 = {"type":"FeatureCollection","features": []};
  //     var set5 = {"type":"FeatureCollection","features": []};
  //     var set6 = {"type":"FeatureCollection","features": []};
  //     var set7 = {"type":"FeatureCollection","features": []};
  //     var set8 = {"type":"FeatureCollection","features": []};
  //
  //     set1.features = $geoJson1;
  //     set2.features = $geoJson2;
  //     set3.features = $geoJson3;
  //     set4.features = $geoJson4;
  //     set5.features = $geoJson5;
  //     set6.features = $geoJson6;
  //     set7.features = $geoJson7;
  //     set8.features = $geoJson8;
  //
  // 	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
  // 		maxZoom: 18,
  // 		attribution: 'CPE - for IRC Students - Stand up for Paris',
  // 		id: 'mapbox.light'
  // 	}).addTo(map);
  //     function onEachFeature(feature, layer) {
  //         var popupContent = "<p>I started out as a GeoJSON " +
  //             feature.geometry.type + ", but now I'm a Leaflet vector!</p>";
  //         if (feature.properties && feature.properties.popupContent) {
  //             popupContent += feature.properties.popupContent;
  //         }
  //         layer.bindPopup(popupContent);
  //     }
  // 	L.geoJSON( set1, {
  // 		style: function (feature) {
  //             return color1
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set2, {
  // 		style: function (feature) {
  //             return color2
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set3, {
  // 		style: function (feature) {
  //             return color3
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set4, {
  // 		style: function (feature) {
  //             return color4
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set5, {
  // 		style: function (feature) {
  //             return color5
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set6, {
  // 		style: function (feature) {
  //             return color6
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set7, {
  // 		style: function (feature) {
  //             return color7
  // 		},
  // 	}).addTo(map);
  //     L.geoJSON( set8, {
  // 		style: function (feature) {
  //             return color8
  // 		},
  // 	}).addTo(map);
  // </script>
  // </body>
  // </html>
  // """)
}
