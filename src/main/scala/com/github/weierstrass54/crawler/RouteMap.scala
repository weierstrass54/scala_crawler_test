package com.github.weierstrass54.crawler

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.weierstrass54.crawler.route.CrawlRoute

object RouteMap {

  private val crawlRoute = new CrawlRoute()

  val routeMap: Route =
    crawlRoute.route ~
      path("healthz") {
        get {
          complete("OK")
        }
      }

}
