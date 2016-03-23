package chpater_04_akka_persistence.Ex01_PersistentActorCounter

import akka.actor.{Props, ActorSystem}
import akka.persistence.{SnapshotOffer, PersistentActor}
import chpater_04_akka_persistence.Ex01_PersistentActorCounter.PersistentCounter._

object PersistentCounter {
  sealed trait Operation {
    val count: Int
  }

  case class Inc(override val count: Int) extends Operation
  case class Dec(override val count: Int) extends Operation

  case class Cmd(op: Operation)
  case class Evt(op: Operation)

  case class State(count: Int)
}

class PersistentCounter extends PersistentActor {

  var state = State(0)

  def update(op : Operation) = op match {
    case Inc(x) =>
      state = State(state.count + x)
      println(s"New state $state")
    case
      Dec(x) => state = State(state.count - x)
      println(s"New state $state")
  }

  override def receiveRecover: Receive = {
    case evt @ Evt(op) =>
      println(s"Recover from $evt")
      update(op)
    case SnapshotOffer(_, snapshot: State) =>
      state = snapshot
  }

  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"Received command $cmd")
      persist(Evt(op)) {
        evt => update(evt.op)
      }
  }

  override def persistenceId: String = "counter-persistance-actor"
}

object Main extends App{
  val system = ActorSystem("persistent")
  val actor = system.actorOf(Props[PersistentCounter])

  actor ! Cmd(Inc(2))
  actor ! Cmd(Inc(5))
  actor ! Cmd(Dec(7))
  actor ! Cmd(Dec(9))
}