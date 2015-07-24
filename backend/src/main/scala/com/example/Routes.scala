package com.example

import akka.http.scaladsl.server.Directives._

trait Routes {

  val routes = get {
    path("double") {
      parameter('num.as[Int]) { num =>
        complete(s"${num * 2}")
      }
    }
  }

}