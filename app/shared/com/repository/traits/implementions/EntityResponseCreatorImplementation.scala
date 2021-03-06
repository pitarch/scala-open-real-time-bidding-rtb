package shared.com.repository.traits.implementions

import shared.com.ortb.enumeration.DatabaseActionType
import shared.com.ortb.model.attributes.GenericResponseAttributesModel
import shared.com.ortb.model.results.RepositoryOperationResultModel
import shared.com.ortb.model.wrappers.persistent.EntityWrapperModel
import shared.com.repository.RepositoryBase
import shared.com.repository.traits.EntityResponseCreator
import shared.io.helpers.AdapterHelper
import shared.io.loggers.AppLogger

import scala.concurrent.Future

trait EntityResponseCreatorImplementation[TTable, TRow, TKey]
  extends EntityResponseCreator[TTable, TRow, TKey] {
  this : RepositoryBase[TTable, TRow, TKey] =>
  protected def createResponseForAffectedRowCount(
    affectedRow : Int,
    entity : Option[TRow],
    actionType : DatabaseActionType,
    message : String = "",
    isSuccess : Boolean = true
  ) : RepositoryOperationResultModel[TRow, TKey] = {
    val message2 = getMessageForEntity(Some(affectedRow), actionType, message)
    val hasAffected = affectedRow > 0

    if (hasAffected && entity.isDefined) {
      AppLogger.logEntitiesWithCondition(isLogDatabaseQueryLogs, Seq(entity.get), message2)
    }

    if (hasAffected) {
      return createResponseFor(
        entityId = Some(getEntityIdFromOptionRow(entity)),
        entity = entity,
        actionType = actionType,
        message = message2,
        isSuccess = isSuccess
      )
    }

    throw new Exception(
      s"${ headerMessage } No record affected for operation: $actionType, Entity: $entity"
    )
  }

  protected def createResponseForAffectedRow(
    affectedEntity : Option[TRow],
    affectedRowsCount : Option[Int],
    actionType : DatabaseActionType,
    message : String = "",
    isSuccess : Boolean = true
  ) : RepositoryOperationResultModel[TRow, TKey] = {
    val message2 : String =
      getMessageForEntity(
        affectedRowsCount,
        actionType,
        message)

    if (affectedEntity != null) {
      AppLogger.logEntityNonFuture(
        isLogDatabaseQueryLogs,
        affectedEntity,
        message2
      )

      val response = createResponseFor(
        entityId = Some(getEntityIdFromOptionRow(affectedEntity)),
        entity = affectedEntity,
        actionType = actionType,
        message = message2,
        isSuccess = isSuccess
      )

      return response
    }

    getEmptyResponseFor(actionType)
  }

  def getEmptyResponseFor(actionType : DatabaseActionType)
  : RepositoryOperationResultModel[TRow, TKey] =
    AdapterHelper.repositoryAdapter.getEmptyResponse[TRow, TKey](
      actionType)

  protected def createResponseFor(
    entityId : Option[TKey],
    entity : Option[TRow],
    actionType : DatabaseActionType,
    message : String = "",
    isSuccess : Boolean = true
  ) : RepositoryOperationResultModel[TRow, TKey] = {
    val attributesModel =
      GenericResponseAttributesModel(
        isSuccess,
        id = Some(entity.get.toString),
        ids = None,
        Some(actionType),
        message = message)

    RepositoryOperationResultModel(
      Some(attributesModel),
      Some(EntityWrapperModel(entityId.get, entity.get))
    )
  }

  protected def getMessageForEntity(
    affectedRowsCount : Option[Int],
    actionType : DatabaseActionType,
    message : String) : String = {
    var message2 = message;
    if (message2.isEmpty) {
      var affectedCount = ""
      if (affectedRowsCount.isDefined) {
        affectedCount = s" (affected rows = ${ affectedRowsCount.get })"
      }

      message2 =
        s"${ headerMessage } [$actionType] operation is successful$affectedCount."
    }

    message2
  }

  protected def getEmptyResponseForInFuture(
    actionType : DatabaseActionType
  ) : Future[RepositoryOperationResultModel[TRow, TKey]] = {
    AppLogger.conditionalInfo(
      isLogDatabaseQueryLogs,
      s"${ headerMessage } $actionType is skipped."
    )

    Future {
      getEmptyResponseFor(actionType)
    }
  }
}
