import Checker.{WhiteUser, BlackUser, CheckUser}
import Storage.AddUser
import Recorder.NewUser
import akka.actor.{Props, ActorSystem, ActorRef, Actor}
import akka.pattern.ask
import akka.util.Timeout

/*Pozwala korzystać ze składni 5 seconds w Timeoucie*/
import scala.concurrent.duration._

import scala.collection.mutable

case class User(name:String, email:String)

object Recorder {
  sealed trait RecorderMessage
  case class NewUser(user: User) extends RecorderMessage

  /*Stworzenie aktora z dwoma zależnymi aktorami*/
  def props(checker: ActorRef, storage: ActorRef) = Props(new Recorder(checker, storage))
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  /*Tutaj jako implicit dany jest execution context jak i Timeout*/
  import scala.concurrent.ExecutionContext.Implicits.global
  /*ładnie widać jak liczba konwertowana jest do DurationConversions, a potem wywoływana jest na niej
  * metoda seconds zwracająca Duration*/
  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case NewUser(user) =>
      println(s"New request to add user $user.")
      /*Ciekawe użycie map na Future zwracanym przez aska*/
      checker ? CheckUser(user) map {
        case BlackUser(user) =>
          println(s"It was not possible to add user $user. User is blacklisted.")
        case WhiteUser(user) =>
          println(s"User $user was properly added to the database.")
      };
  }
}

object Checker {
  /*Tutaj ładnie widać jak powinien wyglądać companion object dla aktora
  * Podział na wiadomości, które może aktor dostać
  * jak i zwracane wiadomosć
  * Na końcu znajduje się domyślny props*/
  sealed trait CheckerMessage
  case class CheckUser(user: User) extends CheckerMessage

  sealed trait CheckerResponse
  case class BlackUser(user: User) extends CheckerResponse
  case class WhiteUser(user: User) extends CheckerResponse

  def props = Props[Checker]
}

class Checker extends Actor {
  val blackList = List(User("admin", "admin@nomail.com"))

  override def receive: Actor.Receive = {
    /*Fajnie widać użycie if w case*/
    case CheckUser(user) if blackList.contains(user) =>
      println(s"User $user in on the black list.")
      /*W taki sposób zwraca się wiadomość do aktora odpytującego*/
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"User $user is allowed.")
      sender() ! WhiteUser(user)
  }
}

object Storage {
  sealed trait StorageMessage
  case class AddUser(user: User) extends StorageMessage

  def props = Props[Storage]
}

class Storage extends Actor {
  val users = mutable.ArrayBuffer.empty[User]

  override def receive: Actor.Receive = {
    case AddUser(user) =>
      println(s"Adding user $user to the database.")
      users += user
  }
}

object UserRecorder extends App{
  val actorSystem = ActorSystem("UserRecorder")
  val checker = actorSystem.actorOf(Checker.props, "Checker")
  val storage = actorSystem.actorOf(Storage.props, "Storage")
  val recorder = actorSystem.actorOf(Recorder.props(checker, storage))

  recorder ! NewUser(User("admin", "admin@nomail.com"))
  recorder ! NewUser(User("tymon", "tymon@gmail.com"))
}
