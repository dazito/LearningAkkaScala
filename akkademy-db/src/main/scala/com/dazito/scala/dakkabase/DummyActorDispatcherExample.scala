package com.dazito.scala.dakkabase

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * Created by daz on 01/04/2016.
 */
class DummyActorDispatchersExample extends Actor{
    override def receive: Receive = {
        case _ => print("Message received")
    }
}
