name := """frontend"""

version := "1.0-SNAPSHOT"


scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  ws,
  specs2 % Test
)

enablePlugins(PlayScala, DockerPlugin)

routesGenerator := InjectedRoutesGenerator

dockerBaseImage := "java:8-jre"
dockerExposedPorts := Seq(9000)
packageName in Docker := "docker.io/ganesh47/sbt_docker_k8s_frontend"
version in Docker := "latest"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
