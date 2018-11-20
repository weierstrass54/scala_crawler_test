package com.github.weierstrass54.crawler.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.weierstrass54.crawler.CrawlerModel
import spray.json._

class CrawlRoute extends SprayJsonSupport {

  val route: Route = path("crawl") {
    get {
      parameters('url.as[String]) {
        urls => {
          CrawlerModel.crawl(urls)
          complete("Hello " + urls)
        }
      }
    }
    post {
      formFieldMultiMap {
        fields => {
          CrawlerModel.crawl(fields.getOrElse("url", List.empty))
          complete("Hello " + fields)
        }
      } ~
      entity(as[JsValue]) {
        json => {
          CrawlerModel.crawl(json.asInstanceOf[JsArray].elements)
          complete(json.asInstanceOf[JsArray])
        }
      }
    }
  }

}
