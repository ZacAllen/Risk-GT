name := "Risk"
 
version := "1.0" 

lazy val `Risk` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"
val scalatraVersion = "2.5.4"
val httpVersion = "4.5.7"
val gsonVersion = "1.7.1"
val liftVersion = "2.0"
val jacksonVersion = "2.9.8"
val slickVersion = "3.3.0"
val gCloudSQLVersion = "1.0.13"

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "org.scalatra" %% "scalatra" % scalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test",
  "org.apache.httpcomponents" % "httpclient" % httpVersion,
  "com.google.code.gson" % "gson" % gsonVersion,
  "net.liftweb" % "lift-json" % liftVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.typesafe.slick" %% "slick" % slickVersion
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

