package controllers

import akka.actor.Scheduler
import javax.inject._
import play.api.libs.concurrent.Futures
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, dr: DataReader)
                              (implicit val ec: ExecutionContext) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action.async {
    val schemaFileName = "app/schemas/booleanmeasures.csv"
    val dataFileName = "app/data/booleanmeasures.txt"
    val schemas = SchemaReader.readFromFile(schemaFileName)
    val dataAsJson = DataReader.fromFileToJson(dataFileName, schemas)
    Future.sequence(dr.sendRequests(dataAsJson)).map {
      _ => Ok(dataAsJson.toString)
    }
  }
}
