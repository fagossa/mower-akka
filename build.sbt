import sbt.Keys._

val akkaVersion = "2.5.4"

lazy val root = (project in file("."))
        .settings(
          name := "mower-scala",
          version := "1.1.1",
          scalaVersion := "2.12.4",
          libraryDependencies ++= Seq(
            "org.scalatest" %% "scalatest" % "3.0.1" % "test",
            "com.typesafe.akka" %% "akka-actor" % akkaVersion,
            "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test" withSources(),
            "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % "test"
          )
        )
