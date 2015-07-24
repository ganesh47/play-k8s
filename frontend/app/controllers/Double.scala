package controllers

import javax.inject.Inject
import play.api.libs.ws._
import play.api.mvc._

class Double @Inject() (ws: WSClient) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  def request(num: String): WSRequest =
    ws.url("http://backend:9500/double")
      .withQueryString("num" -> num).withRequestTimeout(1000)

  def invoke(num: String) = Action.async {
    request(num).get().map { response =>
      Ok(response.body)
    }
  }
}