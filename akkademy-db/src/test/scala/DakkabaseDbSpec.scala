import org.scalatest.{BeforeAndAfterEach, Matchers, FunSpecLike}
import akka.actor.ActorSystem
import com.dazito.scala.dakkabase.messages.SetRequest
import akka.testkit.TestActorRef
import com.dazito.scala.dakkabase.DakkabaseDb


/**
 * Created by daz on 20/02/2016.
 */
class DakkabaseDbSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {
    implicit val system = ActorSystem()
    describe("Given SetRequest") {
        it("Should place key/value into map") {
            val actorRef = TestActorRef(new DakkabaseDb)
            actorRef ! SetRequest("key", "value")
            val dakkabaseDb = actorRef.underlyingActor
            dakkabaseDb.map.get("key") should equal(Some("value"))
        }
    }

}
