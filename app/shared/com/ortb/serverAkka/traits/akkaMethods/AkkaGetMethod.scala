package shared.com.ortb.serverAkka.traits.akkaMethods

import akka.http.scaladsl.model.HttpResponse
import shared.com.ortb.model.requests.AkkaRequestModel

import scala.concurrent.Future

trait AkkaGetMethod {
  def get(akkaRequest : AkkaRequestModel) : HttpResponse

  def getEventual(akkaRequest : AkkaRequestModel) : Future[HttpResponse] =
    Future {
      get(akkaRequest)
    }
}