package chapter_02_playing_with_actors.Ex03_Supervision

import akka.actor._
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import chapter_02_playing_with_actors.Ex03_Supervision.Child.{RestartException, ResumeException, StopException}
import scala.concurrent.duration._

object ControlInstructions {
  sealed trait ControlInstructionsMessages
  class Stop extends ControlInstructionsMessages
  class Resume extends ControlInstructionsMessages
  class Restart extends ControlInstructionsMessages
}

object Child {
  object StopException extends Exception
  object ResumeException extends Exception
  object RestartException extends Exception
}

class Child extends Actor {

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    println("Child: PreStart")
    super.preStart()
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    println("Child: PostStop")
    super.postStop()
  }

  @throws[Exception](classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("Child: PreRestart")
    super.preRestart(reason, message)
  }

  @throws[Exception](classOf[Exception])
  override def postRestart(reason: Throwable): Unit = {
    println("Child: PostRestart")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case Stop => throw StopException
    case Resume => throw ResumeException
    case Restart => throw RestartException
  }
}

class Parent extends Actor {
  var childRef: ActorRef = _

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    childRef = context.actorOf(Props[Child], "Dziecko")
    super.preStart()
  }

  override def supervisorStrategy: SupervisorStrategy
        = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 seconds){
    case StopException => Stop
    case ResumeException => Resume
    case RestartException => Restart
  }

  override def receive: Actor.Receive = {
    case Stop => childRef ! Stop
    /*Wynik:
      Child: PostStop*/
    case Resume => childRef ! Resume
    /*Wynik:
    * [pusty]
    * => Przy resume wyjątek jest łapany i nic się nie dzieje. Dziecko ani nie jest zatrzymywane, anie tworzone od nowa*/
    case Restart => childRef ! Restart
    /*Wynik:
    Child: PreRestart
    Child: PostStop
    Child: PostRestart
    Child: PreStart*/
  }
}

object Main extends App {
  val system = ActorSystem("Supervision")
  val parent = system.actorOf(Props[Parent], "Parent")
  parent ! Resume
  parent ! Restart
  parent ! Stop
}
