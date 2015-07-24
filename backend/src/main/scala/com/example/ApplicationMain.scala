package com.example

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object ApplicationMain extends Routes with App {
  implicit val system = ActorSystem("MyActorSystem")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()
  
  Http().bindAndHandle(routes, "0.0.0.0", 9500)
}