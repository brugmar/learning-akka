package chapter_03_working_with_akka_basic_tools.Ex05_RoutersFromAkka

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig

class Worker extends Actor {

  val name = util.Random.nextInt(1000)

  override def receive: Receive = {
    case text: String => println(s"Worker[$name]: $text")
  }
}

object RandomRouterFromConfig extends App {
  val system = ActorSystem("system")

  val router = system.actorOf(FromConfig.props(Props[Worker]), "ex05-random-router")

  router ! "Message One"
  router ! "Message Two"
  router ! "Message Threes"
}
