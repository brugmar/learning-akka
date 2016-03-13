package chapter_03_working_with_akka_basic_tools.Ex05_RoutersFromAkka

import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinPool
import learning.akka.global.PrintActor
import learning.akka.global.PrintActor.PrintMessage

object RoundRobin extends App {
  val system = ActorSystem("system")
  val router = system.actorOf(RoundRobinPool(3).props(Props[PrintActor]), "RoundRobinPoolRouter")

  router ! PrintMessage("dawniej spadano")
  router ! PrintMessage("i wznoszono się")
  router ! PrintMessage("pionowo")
  router ! PrintMessage("obecnie")
  router ! PrintMessage("spada się")
  router ! PrintMessage("poziomo")
}
