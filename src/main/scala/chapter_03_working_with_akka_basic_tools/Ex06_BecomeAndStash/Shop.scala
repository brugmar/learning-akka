package chapter_03_working_with_akka_basic_tools.Ex06_BecomeAndStash

import akka.actor.{Stash, Props, ActorSystem, Actor}
import chapter_03_working_with_akka_basic_tools.Ex06_BecomeAndStash.ShopActor.{Buy, Close, Open}

object ShopActor {
  trait ShopMessage
  object Open extends ShopMessage
  object Close extends ShopMessage
  case class Buy(product: String)

}
/*To use stash we have to mix it in*/
class ShopActor extends Actor with Stash {
  override def receive: Receive = closed

  def closed: Receive = {
    case Open =>
      unstashAll
      println("Opening shop.")
      context.become(opened)
    case Close => println("Shop is already closed.")
    case Buy(_) =>
      stash
      println("Shop's already closed.")
  }

  def opened: Receive = {
    case Open => println("Shop is already opened.")
    case Close =>
      println("Closing shop.")
      context.unbecome()
    case Buy(product) => println(s"Customer bought product [$product].")
  }
}

object ShopApp extends App {
  val system = ActorSystem("system")
  val shop = system.actorOf(Props[ShopActor], "shop")

  shop ! Open
  shop ! Buy("Margaryna")
  shop ! Buy("Kumkwat")
  shop ! Close
  shop ! Buy("Nils Frahm Record")
  Thread.sleep(1000)
  shop ! Open
}
