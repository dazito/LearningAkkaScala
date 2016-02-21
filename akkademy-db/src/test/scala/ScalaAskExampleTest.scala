import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.dazito.scala.dakkabase.ScalaPongActor
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

    describe("Pong actor") {
        it("should responde with Pong") {
            val future = pongActor ? "Ping" // Uses the implicit timeout
            // The acotr in untyped so it returns a Future[AnyRef], we use mapTo[String] to "cast" it to a Sring
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
}
