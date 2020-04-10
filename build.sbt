name := "scala-rtb-example"
version := "1.0"
scalaVersion := "2.13.1"

lazy val log4Version = "2.11.0"
lazy val akkaVersion = "2.6.4"
lazy val akkaHttpVersion = "10.1.11"
lazy val circeVersion = "0.12.3"

// More details at https://github.com/tototoshi/slick-joda-mapper
lazy val slickVersion = "3.3.2"
lazy val slickJodaMapperVersion = "2.4.0"

libraryDependencies ++= Seq(
  ///////////////////////////////////////////////////
  // Akka core
  ///////////////////////////////////////////////////

  // "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.31.0",
  "com.typesafe.akka" %% "akka-http-caching" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  ///////////////////////////////////////////////////
  // Serialization frameworks
  ///////////////////////////////////////////////////
  
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  
  ///////////////////////////////////////////////////
  // Serialization frameworks : AKKA Enhanced Serializing
  ///////////////////////////////////////////////////

  "io.altoo" %% "akka-kryo-serialization" % "1.0.0" excludeAll(ExclusionRule("com.esotericsoftware", "kryo-shaded")),
  "com.esotericsoftware" % "kryo" % "4.0.2",
  
  ///////////////////////////////////////////////////
  // Logging frameworks
  ///////////////////////////////////////////////////
  
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.apache.logging.log4j" % "log4j-api" % "2.13.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.13.1",
  "io.sentry" % "sentry-log4j2" % "1.7.30",

  ///////////////////////////////////////////////////
  // Persistent or ORM frameworks
  ///////////////////////////////////////////////////

  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperVersion,
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",

  ///////////////////////////////////////////////////
  // Unit Test Frameworks
  ///////////////////////////////////////////////////

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test

  ///////////////////////////////////////////////////
  // Others
  ///////////////////////////////////////////////////
)
