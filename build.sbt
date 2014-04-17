name := "image-stocker"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.play" % "play-slick_2.10" % "0.6.0.1",
  "mysql" % "mysql-connector-java" % "[5.1,)"
)

play.Project.playScalaSettings
