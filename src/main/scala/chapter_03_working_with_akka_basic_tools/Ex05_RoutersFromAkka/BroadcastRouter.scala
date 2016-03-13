package chapter_03_working_with_akka_basic_tools.Ex05_RoutersFromAkka

import akka.actor.{Props, ActorSystem}
import akka.routing.BroadcastPool
import learning.akka.global.PrintActor
import learning.akka.global.PrintActor.PrintMessage

object BroadcastRouter extends App{
  val system = ActorSystem("system")
  val echo = system.actorOf(BroadcastPool(3).props(Props[PrintActor]), "echo")

  echo ! PrintMessage("Yeeaahhh!")
}
