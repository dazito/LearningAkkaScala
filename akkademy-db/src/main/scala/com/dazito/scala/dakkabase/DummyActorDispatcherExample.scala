package com.dazito.scala.dakkabase

import akka.actor.{Actor, Status}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by daz on 01/04/2016.
 */
class DummyActorDispatchersExample extends Actor{
    val blockingIoDispatcher: ExecutionContext = context.system.dispatchers.lookup("blocking-io-dispatcher")

    override def receive: Receive = {
        val s = "Hello"

        case _: String => {
            val future: Future[String] = Future {
                s + " future!"
            }(blockingIoDispatcher)
            future onSuccess {
                case msg => println(msg)
            }
            print("Message received")
        }
        Status.Success
    }
}
