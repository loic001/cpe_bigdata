package viz

import org.fusesource.scalate._

object LeafletRenderer {
  val engine = new TemplateEngine

  val leafletTemplate = "data/html/leaflet01.mustache"

  def render(geojsonArrayString: String, outFile: String) = {
    val output = engine.layout(leafletTemplate, Map("geojsonArray" -> geojsonArrayString))


    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets

    Files.write(Paths.get(outFile), output.getBytes(StandardCharsets.UTF_8))
  }

}
