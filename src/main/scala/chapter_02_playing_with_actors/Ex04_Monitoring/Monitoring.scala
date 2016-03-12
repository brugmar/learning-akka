package chapter_02_playing_with_actors.Ex04_Monitoring

import akka.actor._
import akka.actor.Actor.Receive

case class ActorA(actorB : ActorRef) extends Actor {

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    context.watch(actorB)
    super.preStart()
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("Actor A stopped.")
    super.postStop()
  }

  override def receive: Receive = {
    // Warto pamiętać, że case classy są dokładnie dopasowywane,
    // gdyby zrobić:
    // case Terminated
    // wtedy próba byłaby dopasowana do bezargumentowego konstruktora, który nie istnieje
    // i case nie zostałby złapany
    case Terminated(_) => println("ActorB terminated")
  }
}

class ActorB extends Actor {
  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("Actor B stopped.")
    super.postStop()
  }

  override def receive: Actor.Receive = {
    case "terminate" => context.stop(self)
  }
}

object Monitoring extends App {
  val system = ActorSystem("system")
  val actorB = system.actorOf(Props[ActorB], "ActorB")

  // Warto zauważyć jak jest tworzony actorA, alternatywnie można napisać
  // val actorAPrim = system.actorOf(Props(new ActorA(actorB)), "ActorB")
  val actorA = system.actorOf(Props(classOf[ActorA], actorB), "ActorA")
  actorB ! "terminate"
}
