version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "2.2.0"

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  // "org.apache.spark" %% "spark-streaming" % sparkVersion,
  // "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.scalatra.scalate" %% "scalate-core" % "1.8.0"
)


// [Required] Enable plugin and automatically find def main(args:Array[String]) methods from the classpath
//enablePlugins(PackPlugin)

// [Optional] Specify main classes manually
// This example creates `hello` command (target/pack/bin/hello) that calls org.mydomain.Hello#main(Array[String])
//packMain := Map("hello" -> "org.mydomain.Hello")
