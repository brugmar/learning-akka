package chapter_03_working_with_akka_basic_tools.Ex04_GroupRouting

import akka.actor.{Props, ActorSystem, ActorRef, Actor}
import chapter_03_working_with_akka_basic_tools.Ex04_GroupRouting.Worker.{WorkerMessage, PrintMessage}


object Worker {
  sealed trait WorkerMessage
  case class PrintMessage(str:String) extends WorkerMessage
}

class Worker(name: String) extends Actor {
  override def receive: Receive = {
    case PrintMessage(str) => println(s"Worker [$name]: $str")
  }
}

class Router(actorPaths : List[String]) extends Actor {

  override def receive: Actor.Receive = {
    // Take a look on a forward method
    case message:WorkerMessage => {
      context.actorSelection(actorPaths(util.Random.nextInt(actorPaths.length))) forward message
    }
  }
}

object Routing extends App {
  val system = ActorSystem("system")

  var workerList = List("Ada", "Michal", "Wiktor", "Sylwia")

  workerList.foreach {
    workerName => system.actorOf(Props(new Worker(workerName)), workerName)
  }

  var workerPaths = workerList.map(workerName => "/user/" + workerName)

  val router = system.actorOf(Props(new Router(workerPaths)))

  router ! PrintMessage("Hello!")
  router ! PrintMessage("My name is Luiggi.")
  router ! PrintMessage("What's your name?")
}
