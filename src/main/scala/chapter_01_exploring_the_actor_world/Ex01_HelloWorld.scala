import akka.actor._

case class WhoToGreet(name: String)

class GreetingActor extends Actor {
  override def receive = {
    case WhoToGreet(name) => println(s"Hello $name!")
  }
}

object HelloWorld extends App {
  val actorSystem = ActorSystem("LocalActorSystem")
  val greetingActor = actorSystem.actorOf(Props[GreetingActor], "greeter")

  greetingActor ! WhoToGreet("Elizeusz")
}
