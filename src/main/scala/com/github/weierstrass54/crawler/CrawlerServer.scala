package com.github.weierstrass54.crawler

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger

import scala.concurrent._
import scala.concurrent.duration.Duration

object CrawlerServer extends App {
  private val log = Logger(CrawlerServer.getClass)
  private val host = sys.env.getOrElse("HOST", "localhost")
  private val port = sys.env.getOrElse("PORT", 8080).toString.toInt

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  private val server = Http().bindAndHandle(RouteMap.routeMap, host, port)

  server.failed.foreach(
    e => log.error("Не удалось запустить сервер на {}:{}!", host, port, e)
  )

  server.onComplete {
    case scala.util.Success(_) => log.info("Сервер успешно запущен на {}:{}", host, port)
    case scala.util.Failure(e) => log.error("Ошибка запуска сервера на {}:{}", host, port, e);
  }

  Await.ready(server.flatMap(_ => {
    val promise = Promise[Done]
    sys.addShutdownHook({
      promise.trySuccess(Done)
    })
    promise.future
  }), Duration.Inf)

  server
    .flatMap(_.unbind())
    .onComplete(_ => {
      log.info("Завершаю работу сервера {}:{}..", host, port)
      actorSystem.terminate()
    })

}
