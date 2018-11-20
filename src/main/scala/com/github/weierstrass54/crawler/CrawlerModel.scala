package com.github.weierstrass54.crawler

import spray.json.JsValue

object CrawlerModel {

  //todo: exceptionHandler on IllegalArgumentException
  //todo: возможно убрать RouteMap
  //todo: рисование Map в responseEntity
  //todo: параллельное исполнение(?)
  //todo: валидация url
  //todo: соббсно сам crawler

  def crawl(url: String): Map[String, String] = {
    println(url)
    null
  }

  def crawl(url: List[String]): Map[String, String] = {
    url.foreach{ println }
    null
  }

  def crawl(url: Vector[JsValue]): Map[String, String] = {
    url.foreach{ println }
    null
  }

}
