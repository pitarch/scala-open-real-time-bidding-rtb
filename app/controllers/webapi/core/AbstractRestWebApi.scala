package controllers.webapi.core

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import play.api.mvc.{Action, _}
import services.core.AbstractBasicPersistentService
import services.core.traits.BasicPersistentServiceContracts
import shared.com.ortb.enumeration._
import shared.com.ortb.model.wrappers.http._
import shared.com.ortb.model.wrappers.persistent.EntityWrapperWithOptions
import shared.io.loggers.AppLogger

abstract class AbstractRestWebApi[TTable, TRow, TKey]
(components : ControllerComponents)
  extends
    AbstractController(components) with
    RestWebApiContracts[TTable, TRow, TKey] {

  val service : AbstractBasicPersistentService[TTable, TRow, TKey]
  val noContentMessage = "No content in request."

  def getAll : Action[AnyContent] = Action { implicit request =>
    val campaigns = service.getAll
    val json = "" // campaigns.asJson.spaces2
    Ok(json)
  }

  def byId(id : TKey) : Action[AnyContent] = Action { implicit request =>
    val entity = service.getById(id)
    val json = "" // entity.get.asJson.spaces2
    Ok(json)
  }

  def add() : Action[AnyContent] = Action { implicit request =>
    try {
      val entityResponseWrapper = bodyRequestToEntity(request);
      val isDefinedProperly = entityResponseWrapper.isDefined &&
        entityResponseWrapper.get.entityWrapper.isDefined

      if (isDefinedProperly) {
        // has item
        val entityWrapper = entityResponseWrapper.get.entityWrapper.get
        val entity = entityWrapper
        val response = service.addUsingOption(entity.entity).get
        val successMessageToString = successMessage(None)
        val responseEntity = response.entity

        if (response.isSuccess && responseEntity.isDefined) {
          val entityWrapper = Some(EntityWrapperWithOptions[TRow, TKey](
            None,
            responseEntity))

          val httpSuccessWrapper = HttpSuccessActionWrapper[TRow, TKey](
            additionalMessage = Some(successMessageToString),
            resultType = Some(HttpActionWrapperType.PutOk),
            entityWrapper = entityWrapper,
            rawBodyRequest = request.body.asText,
            databaseActionType = Some(DatabaseActionType.Create))

          return performOkayOnEntity(Some(httpSuccessWrapper))
        }
      }

      val operationFailedMessage = failedMessage(databaseActionType = Some(DatabaseActionType.Create))

      val httpFailedActionWrapper = HttpFailedActionWrapper[TRow, TKey](
        additionalMessage = Some(operationFailedMessage),
        resultType = Some(HttpActionWrapperType.PutFailed),
        rawBodyRequest = request.body.asText,
        databaseActionType = Some(DatabaseActionType.Create))

      return performBadRequest(Some(httpFailedActionWrapper))
    } catch {
      case e : Exception => AppLogger.error(e)
        val httpFailedExceptionActionWrapper = HttpFailedExceptionActionWrapper[TRow, TKey](
          exception = Some(e),
          resultType = Some(HttpActionWrapperType.PutFailed),
          rawBodyRequest = request.body.asText,
          databaseActionType = Some(DatabaseActionType.Create))

        return performBadRequestOnException(httpFailedExceptionActionWrapper)
    }
  }

  def update(id : TKey) : Action[AnyContent]

  def delete(id    : TKey) : Action[AnyContent]

  def addEntities() : Action[AnyContent]

  def addEntitiesBySinge() : Action[AnyContent]
}
