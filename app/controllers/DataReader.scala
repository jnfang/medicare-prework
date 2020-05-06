package controllers

import akka.actor.{ActorSystem, Scheduler}
import akka.pattern.Patterns
import controllers.SchemaReader._
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.io.Source
import akka.pattern.after

import scala.util.control.NonFatal

@Singleton
class DataReader @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  def sendRequests(data: List[JsObject]): List[Future[WSResponse]] = {
    data.map {
      dataForOneRequest =>
        val request = ws.url("https://2swdepm0wa.execute-api.us-east-1.amazonaws.com/prod/NavaInterview/measures")
        val result = request.addHttpHeaders("Content-Type" -> "application/json").withRequestTimeout(Duration(5000,"millis")).post(dataForOneRequest)
        result
    }
  }

  /* Async retrier
  TODO: Add module binding for Akka Scheduler
  def retryRequest[T](responseFuture: => Future[T], delay:FiniteDuration = FiniteDuration(1, "seconds"), retries: Int = 5)(implicit s: Scheduler): Future[T] = {
    responseFuture.recoverWith {
      case NonFatal(_) if retries > 0 =>
        val nextDelay = FiniteDuration(Math.ceil(delay.length*2).toInt, delay.unit)
        after(delay, s)(retryRequest(responseFuture, nextDelay, retries - 1))
    }
  }
  */
}

object DataReader {
  def fromFileToJson (fileName: String, schema: List[SchemaColumn]): List[JsObject] = {
    val dataFile = Source.fromFile(fileName)
    val lines = dataFile.getLines.toList
    dataFile.close
    stringsToJson(lines, schema)
  }

  private def stringsToJson(lines: List[String], schema: List[SchemaColumn]): List[JsObject] = {
    lines.map{
      line =>
        val reqBody = schema.map {
          schema =>
            val content = if (schema.index < line.length){
              line.substring(schema.index - schema.width, schema.index).trim
            } else {
              line.substring(schema.index - schema.width).trim
            }
            schema.dataType match {
              case Text => schema.name -> JsString(content)
              case Integer => schema.name -> JsNumber(content.toInt)
              case Boolean =>
                val boolValue = if (content.toInt == 1){
                  JsTrue
                } else {
                  JsFalse
                }
                schema.name -> boolValue
            }
        }
        JsObject(reqBody.toMap)
    }
  }

}
