import akka.actor.Status.{Success}
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.dazito.scala.dakkabase.{DakkabaseDb, ScalaPongActor}
import com.dazito.scala.dakkabase.messages.{ListSetRequest, SetRequest}
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Promise, Await, Future}

/**
 * Created by daz on 21/02/2016.
 */
class ScalaAskExampleTest extends FunSpecLike with Matchers {
    val system = ActorSystem()
    implicit val timeout = Timeout(5 seconds);
    val pongActor = system.actorOf(Props(classOf[ScalaPongActor]))
    val dakkabaseActor = system.actorOf(Props(classOf[DakkabaseDb]))

    describe("Pong actor") {
        it("should responde with Pong") {
            val future = pongActor ? "Ping" // Uses the implicit timeout
            // The actor is untyped so it returns a Future[AnyRef], we use mapTo[String] to "cast" it to a String
            val result = Await.result(future.mapTo[String], 1 second)

            assert(result == "Pong")
        }
        it("should fail on unknown message") {
            val future = pongActor ? "unknown"
            intercept[Exception] {
                Await.result(future.mapTo[String], 1 second)
            }
        }
    }
    
    describe("FutureExamples") {
        import scala.concurrent.ExecutionContext.Implicits.global
        it("Should print to console") {
            (pongActor ? "Ping").onSuccess({
                case x: String => println("Replied with: " + x)
            })

            Thread.sleep(200)
        }

        it("should cause and log a failure"){
            askPong("causeError").onFailure{
                case e: Exception => println("Got Exception")
            }
        }

        it("should fail and recover") {
            val f = askPong("causeError").recover( {
                case t: Exception => "default" // In case of error, return "default"
            })

            f should equal("default")
        }

        it("should recover from failure asynchronously") {
            askPong("causeError").recoverWith({
                case t: Exception => askPong("ping")
            })
        }

        it("composing futures") {
            val future = askPong("ping")
                    .flatMap(message => askPong("Ping" + message))
                    .recover{
                        case _: Exception => "There was an error"
                    }
            val result = Await.result(future, 1 second)
            result should equal("There was an error")
        }

        it("combining futures") {
            val future1 = Future{4}
            val future2 = Future{5}

            val futureAddition: Future[Int] = {
                for (res1 <- future1; res2 <- future2) yield res1 + res2
            }
            val additionResult = Await.result(futureAddition, 1 second)
            assert(additionResult == 9)
        }

        it("dealing with lists of futures") {
            val listOfFutures: List[Future[String]] = List("Pong", "Pong", "failed").map(pong => askPong(pong))
            val futureOfList: Future[List[String]] = Future.sequence(listOfFutures)

            Future.sequence(listOfFutures.map(future => future.recover{case _: Exception => ""}))
        }

        it("dealing with batch insert") {
            val setRequestList = ListSetRequest(List(new SetRequest("batchInsert1", "value")))
            val future = dakkabaseActor ? setRequestList

            val additionResult = Await.result(future, 1 second)

            assert(additionResult.equals(Success))
        }
    }

    def askPong(message: String): Future[String] = (pongActor ? message).mapTo[String]


}
