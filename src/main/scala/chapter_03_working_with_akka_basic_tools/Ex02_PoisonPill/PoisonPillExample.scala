package chapter_03_working_with_akka_basic_tools.Ex02_PoisonPill

import akka.actor.{PoisonPill, Props, ActorSystem, Actor}
import akka.actor.Actor.Receive

class ActorA extends Actor {
  override def receive: Receive = {
    case x => print(x)
  }
}

object PoisonPillExample extends App {
  val s = ActorSystem("AnotherAS")
  val a = s.actorOf(Props[ActorA])
  a ! PoisonPill
  a ! "OtherMsg"
}
