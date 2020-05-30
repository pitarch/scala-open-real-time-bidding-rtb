package shared.com.ortb.serverAkka.traits.akkaMethods

import akka.http.scaladsl.model.HttpResponse
import shared.com.ortb.model.requests.AkkaRequestModel

trait AkkaNonImplementPostMethod extends AkkaPostMethod {
  def post(akkaRequest : AkkaRequestModel) : HttpResponse = throw new NotImplementedError(s"POST ${ akkaRequest.endPointPrefix } not supported.")
}