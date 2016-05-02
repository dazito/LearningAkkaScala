package com.dazito.scala.dakkabase.circuitbreaker

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.pattern.{CircuitBreaker, ask}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import akka.pattern.CircuitBreaker
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main {
    def main(args: Array[String]): Unit = {
        implicit val timeout = Timeout(2, TimeUnit.SECONDS)

        val system = ActorSystem("circuit-breaker-actor-system")
        val pongActor = system.actorOf(Props.create(classOf[PongActor]))

        val circuitBreaker = new CircuitBreaker(
            system.scheduler,
            maxFailures = 3,
            callTimeout = 2 seconds,
            resetTimeout = 1 seconds
        )

        circuitBreaker.onOpen(println("On open!"))
        circuitBreaker.onClose(println("On close!"))
        circuitBreaker.onHalfOpen(println("On half open!"))

        Await.result(pongActor ? "Ping!", Duration.create(2, TimeUnit.SECONDS))

        (1 to 100000).foreach(x => {
            Thread.sleep(50)
            val askFuture = circuitBreaker.withCircuitBreaker(pongActor ? "Ping")
            askFuture.map(x => "Got it: " + x).recover({
                case t => "error: " + t.toString
            }).foreach(x => println(x))
        })


  }
}
