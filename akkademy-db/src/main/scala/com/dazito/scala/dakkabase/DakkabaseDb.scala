package com.dazito.scala.dakkabase

import akka.actor.SupervisorStrategy.{Stop, Restart, Escalate, Resume}
import akka.actor._
import akka.event.Logging
import com.dazito.scala.dakkabase.exceptions.{BokenPlateException, DrunkFoolException, TiredChefException, RestautantFireError}
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

            // Let the send know it succeed
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
}
