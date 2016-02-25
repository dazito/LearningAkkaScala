package com.dazito.scala.dakkabase

import akka.actor.{Status, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import com.dazito.scala.dakkabase.messages.{UnknownMessageException, KeyNotFoundException, GetRequest, SetRequest}

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
        case o => {
            log.info("Received unknown message: {}", o)
            Status.Failure(new UnknownMessageException())
        }
    }

}
