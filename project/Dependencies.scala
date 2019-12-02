import sbt._

object Dependencies {
  lazy val kafkaStreamsJava = "org.apache.kafka" % "kafka-streams" % "2.3.0"
  lazy val kafkaStreamsScala = "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0"

  lazy val circeCore = "io.circe" %% "circe-core" % "0.12.0-M3"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.12.0-M3"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.12.0-M3"

  lazy val typesafeConfig = "com.typesafe" % "config" % "1.4.0"

  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http"   % "10.1.11"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.26"

  lazy val slick = "com.typesafe.slick" %% "slick" % "3.3.2"
  lazy val postgresDriver = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

  lazy val jwt = "com.pauldijou" %% "jwt-core" % "3.0.1"
  lazy val scalaBcrypt = "com.github.t3hnar" %% "scala-bcrypt" % "4.1"

  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val playLogback = "com.typesafe.play" %% "play-logback" % "2.7.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val slf4jlog = "org.slf4j" % "slf4j-log4j12" % "1.7.25"
  lazy val log4j = "log4j" % "log4j" % "1.2.17"
}
