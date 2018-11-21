package com.github.weierstrass54.crawler

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._

object CrawlRoute extends SprayJsonSupport {

  val route: Route = path("crawl") {
    get {
      parameters('url.as[String]) { urls =>
        {
          complete(CrawlerClient.crawl(urls).toJson)
        }
      }
    } ~
      post {
        formFieldMultiMap { fields =>
          {
            complete(
              CrawlerClient.crawl(fields.getOrElse("url", List.empty)).toJson)
          }
        } ~
          entity(as[JsValue]) { json =>
            {
              complete(
                CrawlerClient.crawl(json.asInstanceOf[JsArray].elements).toJson)
            }
          }
      }
  } ~
    path("healthz") {
      get {
        complete("OK")
      }
    }

}
