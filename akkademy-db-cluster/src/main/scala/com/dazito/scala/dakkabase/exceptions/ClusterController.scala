package com.dazito.scala.dakkabase.exceptions

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, UnreachableMember}
import akka.event.Logging

/**
  * Created by daz on 13/04/2016.
  */
class ClusterController extends Actor {

    val log = Logging(context.system, this)
    val cluster = Cluster(context.system)

    @scala.throws[Exception](classOf[Exception])
    override def preStart(): Unit = {
        cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
    }

    @scala.throws[Exception](classOf[Exception])
    override def postStop(): Unit = {
        cluster.unsubscribe(self)
    }

    override def receive: Receive = {
        case x: MemberEvent => log.info("MemberEvent: {}", x)
        case x: UnreachableMember => log.info("UnreachableMember: {}", x)
    }
}
