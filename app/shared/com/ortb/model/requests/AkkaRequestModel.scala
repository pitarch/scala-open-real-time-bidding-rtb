package shared.com.ortb.model.requests

import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import shapeless.Lazy
import shared.com.ortb.model.routing.PlaceHolderRoutingModel
import shared.com.ortb.serverAkka.framework.restClient.softler.context.AkkaHttpContext._
import shared.com.ortb.serverAkka.traits.AkkHttpServerContracts
import shared.io.extensions.TypeConvertExtensions._

import scala.concurrent.Future

case class AkkaRequestModel(httpRequest : HttpRequest, akkaServer : AkkHttpServerContracts) {
  lazy val uri : Uri = httpRequest.uri
  lazy val headers : Seq[HttpHeader] = httpRequest.headers
  lazy val entity : RequestEntity = httpRequest.entity
  lazy val query : Uri.Query = uri.query()
  lazy val eventualEntityString : Future[String] = Unmarshal(entity).to[String]
  lazy val entityString : String = eventualEntityString.toRegular()
  lazy val relativePath : String = uri.path.toString()
  lazy val fullPath : String = uri.toString()
  lazy val queryStrings : String = query
    .map(w => s"${ w._1 } => ${ w._2 }")
    .toJoinStringLineSeparator

  def getObjectFromEntityJson[T](implicit decoder : Lazy[DerivedDecoder[T]],
                                 encoder : Lazy[DerivedAsObjectEncoder[T]]) : T =
    entityString.asObjectFromJson[T](decoder, encoder)

  def getObjectsFromEntityJson[T](implicit decoder : Lazy[DerivedDecoder[T]],
                                  encoder : Lazy[DerivedAsObjectEncoder[T]]) : Iterable[T] =
    entityString.asObjectsFromJson[T](decoder, encoder)

  lazy val isHttpGetMethod : Boolean = httpRequest.method == HttpMethods.GET
  lazy val isHttpPostMethod : Boolean = httpRequest.method == HttpMethods.POST
  lazy val isHttpPutMethod : Boolean = httpRequest.method == HttpMethods.PUT
  lazy val isHttpDeleteMethod : Boolean = httpRequest.method == HttpMethods.DELETE
  lazy val isHttpPatchMethod : Boolean = httpRequest.method == HttpMethods.PATCH
  lazy val httpMethodName : String = httpRequest.method.toString()

  lazy val routingPlaceHolderModel : PlaceHolderRoutingModel = PlaceHolderRoutingModel(this)
}
