scalaVersion := "2.12.8"

import Dependencies._

lazy val root = (project in file("."))
    .settings(
      name := "dm874-auth",
      organization := "dm874",
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



