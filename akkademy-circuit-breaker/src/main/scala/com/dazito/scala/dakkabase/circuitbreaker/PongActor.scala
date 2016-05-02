package com.dazito.scala.dakkabase.circuitbreaker

import akka.actor.{Actor, Status}

/**
  * Created by daz on 02/05/2016.
  */
class PongActor extends Actor {
    override def receive: Receive = {
        case x => {
            Thread.sleep(150)
            println("Pong!")
            sender() ! Status.Success
        }
    }
}
