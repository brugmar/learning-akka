package chapter_03_working_with_akka_basic_tools.Ex05_RoutersFromAkka

import akka.actor.{ActorSystem, Props}
import akka.routing.RandomGroup
import learning.akka.global.PrintActor
import learning.akka.global.PrintActor.PrintMessage


object GroupRouter extends App {
  val system = ActorSystem("system")

  val actorNames = List("A1", "A2", "A3")

  actorNames.foreach(actorName => system.actorOf(Props[PrintActor], actorName))

  val paths = actorNames.map(name => "/user/" + name)

  val groupRouter = system.actorOf(RandomGroup(paths).props(), "GroupRouter")

  groupRouter ! PrintMessage("I")
  groupRouter ! PrintMessage("Me")
  groupRouter ! PrintMessage("Myself")

}
