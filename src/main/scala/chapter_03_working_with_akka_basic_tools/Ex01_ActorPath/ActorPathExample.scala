package chapter_03_working_with_akka_basic_tools.Ex01_ActorPath

import akka.actor._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask

class ActorA extends Actor {
  override def receive: Receive = {
    case msg => println(s"Received message $msg.")
  }
}

object ActorPathExample extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout:Timeout = 5 seconds

  val system = ActorSystem("system")
  val actorARef = system.actorOf(Props[ActorA], "actorA")
  println(s"ActorRef for ActorA is $actorARef")
  actorARef ! "--> message through actorRef"

  val actorSelection = system.actorSelection("/user/actorA")
  println(s"ActorSelection for ActorA is $actorSelection")
  /* Jeżeli dany path nie będzie istniał to message trafi do Dead Letters */
  actorSelection ! "--> message through actorSelection"

  actorSelection.resolveOne() map {
    case actorRef: ActorRef => println(s"ActorRef from ActorSelection $actorRef.")
  }

  actorSelection ? Identify(None) map {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Another method to get ActorRef from ActorSelection $ref")
    case ActorIdentity(_, None) =>
      println(s"No ActorRef for that ActorSelection")
  }
}
