package com.dazito.scala.dakkabase

import akka.actor.Actor
import akka.actor.Actor.Receive

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by daz on 01/04/2016.
 */
class DummyActorDispatchersExample extends Actor{
    val blockingIoDispatcher: ExecutionContext = context.system.dispatchers.lookup("blocking-io-dispatcher")

    override def receive: Receive = {
        val s = "Hello"
        case _ => {
            val future: Future[String] = Future {
                s + " future!"
            }(blockingIoDispatcher)
            future onSuccess {
                case msg => println(msg)
            }
            print("Message received")
        }
    }
}
