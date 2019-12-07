scalaVersion := "2.12.8"

import Dependencies._

lazy val root = (project in file("."))
    .settings(
      name := "hello-world",
      organization := "ch.epfl.scala",
      version := "1.0",
      libraryDependencies ++= Seq(
            circeCore,
            circeParser,
            circeGeneric,
            typesafeConfig,
            akkaHttp,
            akkaStream,
            slick,
            postgresDriver,
            jwt,
            scalaBcrypt,
            scalaLogging,
            slf4jlog,
            log4j,
            hikari
      )
)



