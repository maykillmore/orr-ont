import sbt._
import Keys._
import org.scalatra.sbt._


object build extends Build {
  val Organization = "org.mmisw"
  val Name = "ORR Ont"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.2"
  val ScalatraVersion = "2.2.2"

  lazy val project = Project (
    "orr-ont",
    file("."),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.mongodb"               %% "casbah"               % "2.6.0",
        "org.scalatra"              %% "scalatra"             % ScalatraVersion,
        "org.scalatra"              %% "scalatra-specs2"      % ScalatraVersion % "test",
        "com.typesafe"               % "config"               % "1.2.0",
        "org.scalatra"              %% "scalatra-json"        % ScalatraVersion,
        "org.json4s"                %% "json4s-native"        % "3.2.5",
        "org.json4s"                 % "json4s-jackson_2.10"  % "3.2.5",
        "org.json4s"                 % "json4s-mongo_2.10"    % "3.2.5",
        "com.typesafe"              %% "scalalogging-slf4j"   % "1.0.1",

        "org.jasypt"                 % "jasypt"               % "1.9.1",

        "ch.qos.logback"             % "logback-classic"      % "1.0.6" % "runtime",

        // if creating standalone, uncomment code in JettyLauncher and set "container;compile" here:
        "org.eclipse.jetty"          % "jetty-webapp"         % "8.1.8.v20121106" % "container",

        "org.eclipse.jetty.orbit"    % "javax.servlet"        % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
      )
    )
  )
}
