package com.dazito.scala.dakkabase

import akka.actor.{Status, Actor}
import akka.actor.Actor.Receive

/**
 * Created by daz on 21/02/2016.
 */
class ScalaPongActor extends Actor {
    override def receive: Receive = {
        case "Ping" => sender() ! "Pong"
        case _ => sender() ! Status.Failure(new Exception("Unknown message"))
    }
}
