name := """nava-medicare"""
organization := "nava"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.coursera.naptime" %% "naptime" % "0.11.1"
libraryDependencies += ws
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.31"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "nava.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "nava.binders._"
