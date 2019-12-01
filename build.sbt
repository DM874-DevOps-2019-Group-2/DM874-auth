scalaVersion := "2.12.8"

import Dependencies._

lazy val root = (project in file("."))
    .settings(
      name := "hello-world",
      organization := "ch.epfl.scala",
      version := "1.0",
      libraryDependencies ++= Seq(
            kafkaStreamsJava,
            kafkaStreamsScala,
            circeCore,
            circeParser,
            circeGeneric,
            typesafeConfig
      )
)



