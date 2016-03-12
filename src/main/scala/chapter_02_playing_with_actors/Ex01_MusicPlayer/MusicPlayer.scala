package chapter_02.Ex01_MusicPlayer

import akka.actor.{Actor, ActorSystem, Props}
import chapter_02.Ex01_MusicPlayer.MusicController._
import chapter_02.Ex01_MusicPlayer.MusicPlayer._


object MusicController {
  sealed trait MusicControllerMessage
  case object Start extends MusicControllerMessage
  case object Stop extends MusicControllerMessage

  // recommended way of defining props
  def props = Props[MusicController]
}

class MusicController extends Actor {
  def receive = {
    case Stop => println("Stopped playing music.")
    case Start => println("Started playing music.")
    case _ => println("Unknown message")
  }
}

object MusicPlayer {
  sealed  class MusicPlayerMessage
  case object StopMusic extends MusicPlayerMessage
  case object StartMusic extends MusicPlayerMessage
}

class MusicPlayer extends Actor{
  def receive = {
    case StopMusic => println("I don't want to stop playing music")
    case StartMusic =>
      // Aby stworzyć podaktora trzeba skorzystać z context
      val musicControllerActor = context.actorOf(MusicController.props, "MusicControlerActor")
      musicControllerActor ! Start
  }
}

object Main extends App {
  val actorSystem = ActorSystem("MusicPlayerSystem")
  val musicPlayerActor = actorSystem.actorOf(Props[MusicPlayer], "MusicPlayerActor")
  musicPlayerActor ! StartMusic
}
