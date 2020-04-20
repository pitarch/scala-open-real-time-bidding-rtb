package shared.com.repository.traits.implementions.operations.mutations.async

import shared.com.ortb.enumeration.DatabaseActionType
import shared.com.ortb.model.repository.response.RepositoryOperationResultModel
import shared.com.repository.RepositoryBase
import shared.com.repository.traits.operations.mutations.async.RepositoryUpdateOperationsAsync
import shared.io.loggers.AppLogger
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Future

trait RepositoryUpdateOperationsAsyncImplementation[TTable, TRow, TKey]
  extends RepositoryUpdateOperationsAsync[TTable, TRow, TKey] {
  this : RepositoryBase[TTable, TRow, TKey] =>

  /**
   * if entityId is not matching with given entity id then recreates new entity and set the id given and then perform
   * the action.
   *
   * @param entityId
   * @param entity
   *
   * @return
   */
  def updateAsync(
    entityId          : TKey,
    entity            : TRow
  ) : Future[RepositoryOperationResultModel[TRow, TKey]] = {
    val actionType = DatabaseActionType.Update

    try {
      if (entityId != getEntityId(Some(entity))) {
        val entity2 = setEntityId(Some(entityId), Some(entity))

        return this.saveAsync(
          entity = entity2,
          dbAction = getQueryById(entityId).update(entity2.get),
          DatabaseActionType.Update
        )
      }

      return this.saveAsync(
        entity = Some(entity),
        dbAction = getQueryById(entityId).update(entity),
        DatabaseActionType.Update
      )
    } catch {
      case e : Exception =>
        AppLogger.error(
          e,
          s"${headerMessage} Update failed on [id:$entityId, entity: $entity]"
        )
    }

    getEmptyResponseForInFuture(actionType)
  }
}