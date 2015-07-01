name := "mower-scala"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT",
  "junit" % "junit" % "4.8.1"
)

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
