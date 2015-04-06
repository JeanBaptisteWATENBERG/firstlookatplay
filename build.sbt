name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.scribe" % "scribe" % "1.3.5"
)

//libraryDependencies += "org.scribe" % "scribe" % "1.3.7"

fork in run := true