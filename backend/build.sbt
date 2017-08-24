name := """backend"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"             % "2.3.12",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0"
)

enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage := "java:8-jre"
dockerExposedPorts := Seq(9500)
packageName in Docker := "docker.io/ganesh47/sbt_docker_k8s_backend"
version in Docker := "latest"
