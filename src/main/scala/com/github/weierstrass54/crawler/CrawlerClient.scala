package com.github.weierstrass54.crawler

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import com.typesafe.scalalogging.Logger
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}

object CrawlerClient extends CrawlerApp {
  private val log = Logger(CrawlerClient.getClass)

  private val TIMEOUT: FiniteDuration = Duration.apply(20, TimeUnit.SECONDS)

  def crawl(url: String): Map[String, String] = crawl(url.split(','))

  def crawl(url: List[String]): Map[String, String] = crawl(url.toArray)

  def crawl(url: Vector[JsValue]): Map[String, String] =
    crawl(url.map(_.convertTo[String]).toArray)

  private def crawl(url: Array[String]): Map[String, String] = {
    val result = mutable.Map[String, String]()
    url.foreach(u => {
      try {
        result put (u, doCrawl(new URL(u)))
      } catch {
        case t: Throwable => result.put(u, t.getMessage)
      }
    })
    result.toMap
  }

  private def doCrawl(url: URL): String = {
    val response =
      Await.result(Http().singleRequest(HttpRequest(uri = url.toString)),
                   TIMEOUT)
    if (response.status.isRedirection()) {
      response.entity.discardBytes()
      response.headers
        .find(_.is("location"))
        .map(h => doCrawl(new URL(h.value)))
        .getOrElse("Received " + response.status.value + " but cannot redirect")
    } else {
      Await.result(
        response.entity
          .toStrict(TIMEOUT)
          .map(_.data.utf8String)
          .map(s => parseTitle(s)),
        TIMEOUT
      )
    }
  }

  private def parseTitle(value: String): String = {
    val start = value.indexOf("<title>")
    val end = value.indexOf("</title>")
    if (start == -1) {
      "Тэг title не найден!"
    } else if (end == -1) {
      "Не найден закрывающий тэг title"
    } else {
      value.substring(start + 7, end)
    }
  }

}
