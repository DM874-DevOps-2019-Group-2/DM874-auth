import sbt._

object Dependencies {
  lazy val kafkaStreamsJava = "org.apache.kafka" % "kafka-streams" % "2.3.0"
  lazy val kafkaStreamsScala = "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0"

  lazy val circeCore = "io.circe" %% "circe-core" % "0.12.0-M3"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.12.0-M3"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.12.0-M3"

  lazy val typesafeConfig = "com.typesafe" % "config" % "1.4.0"
}
