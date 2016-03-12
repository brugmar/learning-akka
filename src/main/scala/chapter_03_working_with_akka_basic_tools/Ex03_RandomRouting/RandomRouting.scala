package chapter_03_working_with_akka_basic_tools.Ex03_RandomRouting

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import chapter_03_working_with_akka_basic_tools.Ex03_RandomRouting.Worker.{WorkerMessage, PrintMessage}

object Worker {
  sealed trait WorkerMessage
  case class PrintMessage(str:String) extends WorkerMessage
}

class Worker(name: String) extends Actor {
  override def receive: Receive = {
    case PrintMessage(str) => println(s"Worker [$name]: $str")
  }
}

class Router extends Actor {

  var workers: List[ActorRef] = _

  override def preStart(): Unit = {
    super.preStart()
    workers = (0 to 5 toList) map {
      ind => context.actorOf(Props(new Worker(ind.toString)))
    }
  }

  override def receive: Actor.Receive = {
    // Take a look on a forward method
    case message:WorkerMessage => {
      workers(util.Random.nextInt(5)) forward message
    }
  }
}

object Routing extends App {
  val system = ActorSystem("system")
  val router = system.actorOf(Props[Router])

  router ! PrintMessage("Hello!")
  router ! PrintMessage("My name is Luiggi.")
  router ! PrintMessage("What's your name?")
}
