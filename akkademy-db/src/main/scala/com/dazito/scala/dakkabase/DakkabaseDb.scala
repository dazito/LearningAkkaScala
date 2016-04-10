package com.dazito.scala.dakkabase

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor._
import akka.event.Logging
import akka.routing.{BalancingPool, Broadcast, RoundRobinGroup, RoundRobinPool}
import com.dazito.scala.dakkabase.exceptions.{BokenPlateException, DrunkFoolException, RestautantFireError, TiredChefException}
import com.dazito.scala.dakkabase.messages._

import scala.collection.mutable


/**
 * Created by daz on 20/02/2016.
 */
class DakkabaseDb extends Actor{

    val map = new mutable.HashMap[String, Object]()
    val log = Logging(context.system, this)

    override def receive: Receive = {
        case SetRequest(key, value) => {
            log.info("Received Set Request - key: {} | value: {}", key, value)
            map.put(key, value)

            // Let the sender know it succeed
            sender() ! Status.Success
        }
        case GetRequest(key) => {
            log.info("Received Get Request - key: {}", key)
            val response: Option[Object] = map.get(key)
            response match {
                case Some(x) => sender() ! x
                case None => sender() ! Status.Failure(new KeyNotFoundException(key))
            }
        }
        case SetIfNotExists(key, value) => {
            log.info("Received Set If Noy Exists - key: {} - value  {}", key, value)
            if ( ! map.contains(key) ) {
                map.put(key, value)
            }
            sender() ! Status.Success
        }
        case DeleteMessage(key) => {
            log.info("Received Delete Message - key: {}", key)
            map.remove(key)

            sender() ! Status.Success
        }
        case ListSetRequest(list) => {
            log.info("Received List Set Request - size: {}", list.size)
            list.foreach(setRequest => map.put(setRequest.key, setRequest.value))

            sender() ! Status.Success
        }
        case o => {
            log.info("Received unknown message: {}", o)
            Status.Failure(new UnknownMessageException())
        }
    }

    override def supervisorStrategy: SupervisorStrategy = {
        OneForOneStrategy() {
            case _: BokenPlateException => Resume
            case _: DrunkFoolException => Restart
            case _: RestautantFireError => Escalate
            case _: TiredChefException => Stop
            case _ => Escalate
        }
    }
}

object Main extends App {
    val system = ActorSystem.create("dakkabase-scala")
    system.actorOf(Props[DakkabaseDb], name = "dakkabase-db")

    // Create a router actor with 8 actors
    // We can also pass a supervision startegy to the workerRouter to apply in the routees
    // .withSupervision(strategy) on the RoundRobinPool object
    system.actorOf(Props.create(classOf[ArticleParserActor]).withRouter(new RoundRobinPool(8)))

    // Create a router with a predefined list os actors
    val actors: scala.collection.immutable.Iterable[String] = scala.collection.immutable.Iterable("pathToActors")
    val router: ActorRef = system.actorOf(RoundRobinGroup(actors).props(), "router")

    // Broadcast a message to all the actors in the pool/group
    // router ! Broadcast("Broadcast message!")

    // Dispatchers
    val actorWithDispatcher = system.actorOf(Props.create(classOf[DummyActorDispatchersExample]).withDispatcher("custom-dispatcher"))

    val actorList: List[ActorRef] = (0 to 5).map(x => {
        system.actorOf(Props(classOf[ArticleParserActor]).withDispatcher("article-parsing-dispatcher"))
    }).toList

    val workerRouter = system.actorOf(RoundRobinGroup(actorList.map(x => x.path.toStringWithoutAddress).toList).props(), "workerRouter")
    workerRouter ! new ParseArticle("<strong>Some HTML</strong>")

    // Balancing Pool with a custom dispatcher
    val workerRouterBalancingPool = system.actorOf(BalancingPool(8).props(Props(classOf[ArticleParserActor])), "balancing-pool-dispatcher")
}

















