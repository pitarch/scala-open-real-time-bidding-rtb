package shared.com.ortb.serverAkka.traits.akkaMethods

import akka.http.scaladsl.model.HttpResponse
import shared.com.ortb.manager.traits.CreateDefaultContext._
import shared.com.ortb.model.requests.AkkaRequestModel

import scala.concurrent.Future

trait AkkaPostMethod {
  def post(akkaRequest : AkkaRequestModel) : HttpResponse

  def postEventual(akkaRequest : AkkaRequestModel) : Future[HttpResponse]
  = Future {
    post(akkaRequest)
  }
}
