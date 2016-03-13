package chapter_03_working_with_akka_basic_tools.Ex07_FSM

import akka.actor.{Props, ActorSystem, FSM}
import chapter_03_working_with_akka_basic_tools.Ex07_FSM.Shop._

object Shop {
  trait ShopState
  object Opened extends ShopState
  object Closed extends ShopState

  trait ShopData
  case object EmptyData extends ShopData

  trait ShopMessages
  case object Open extends ShopMessages
  case object Close extends ShopMessages
  case class Buy(product: String) extends ShopMessages
}

class Shop extends FSM[ShopState, ShopData]{
  startWith(Closed, EmptyData)

  when(Closed){
    case Event(Open, _) =>
      println("Shop is about to open")
      goto(Opened)
    case Event(_,_) => stay()
  }

  when(Opened){
    case Event(Buy(product), _) =>
      println(s"Customer is buying product $product.")
      stay()
    case Event(Close, _) =>
      println("Shop is about to close")
      goto(Closed)
  }

  initialize()
}

object FSMShop extends App{
  val system = ActorSystem("system")
  val shop = system.actorOf(Props[Shop], "Shop")

  shop ! Open
  shop ! Buy("Coca-cola")
  shop ! Buy("Yellow socks")
  shop ! Close
  shop ! Buy("Red carpet")
}
