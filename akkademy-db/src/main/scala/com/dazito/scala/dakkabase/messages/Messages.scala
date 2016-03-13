package com.dazito.scala.dakkabase.messages

/**
 * Created by daz on 29/02/2016.
 */
case class DeleteMessage(key: String)
case class GetRequest (key: String)
case class KeyNotFoundException(key: String) extends Exception
case class SetIfNotExists(key: String, value: Object)
case class SetRequest(key: String, value: Object)
case class UnknownMessageException () extends Exception
case class Connected()
case class Disconnected()
case class Request()

