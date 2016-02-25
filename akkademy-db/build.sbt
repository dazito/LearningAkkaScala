name := """akkademy-db"""

version := "1.0"

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
    "org.scalatest"     %% "scalatest"    % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.2" % "test",
    "com.typesafe.akka" %% "akka-actor"   % "2.4.2"
)


