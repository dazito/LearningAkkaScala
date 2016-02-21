package com.dazito.scala.dakkabase

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.event.Logging
import com.dazito.scala.dakkabase.messages.SetRequest

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
        }
        case o => {
            log.info("Received unknown message: {}", o)
        }
    }

}
