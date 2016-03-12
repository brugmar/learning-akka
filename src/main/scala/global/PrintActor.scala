package learning.akka.global

import PrintActor.PrintMessage
import akka.actor.Actor

object PrintActor {
  sealed trait WorkerMessage
  case class PrintMessage(str:String) extends WorkerMessage
}

class PrintActor extends Actor {
  val name = util.Random.nextInt(1000)

  override def receive: Receive = {
    case PrintMessage(str) => println(s"Worker [$name]: $str")
  }
}