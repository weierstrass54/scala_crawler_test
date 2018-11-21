package com.github.weierstrass54.crawler

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait CrawlerApp {

  protected implicit val actorSystem: ActorSystem = ActorSystem()
  protected implicit val actorMaterializer: ActorMaterializer =
    ActorMaterializer()
  protected implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher

}
