package shared.com.repository.traits.implementions.operations.mutations

import shared.com.ortb.enumeration.DatabaseActionType
import shared.com.ortb.model.results.{RepositoryOperationResult, RepositoryOperationResults}
import shared.com.ortb.model.wrappers.persistent.EntityWrapper
import shared.com.repository.RepositoryBase
import shared.com.repository.traits.operations.mutations.RepositoryOperationsAsync
import shared.io.loggers.AppLogger
import slick.jdbc.SQLiteProfile.api._
import slick.dbio.{Effect, NoStream}
import slick.sql.FixedSqlAction

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

trait RepositoryOperationsAsyncImplementation[TTable, TRow, TKey]
  extends RepositoryOperationsAsync[TTable, TRow, TKey] {
  this : RepositoryBase[TTable, TRow, TKey] =>



  def getDeleteAction(
    entityId : TKey
  ) : FixedSqlAction[Int, NoStream, Effect.Write]

  def deleteAsync(
    entityId : TKey
  ) : Future[RepositoryOperationResult[TRow, TKey]] = {
    val entity = getById(entityId)
    val actionType = DatabaseActionType.Delete

    try {
      val deleteAction = getDeleteAction(entityId)

      return this.saveAsync(
        entity = entity,
        dbAction = deleteAction,
        actionType
      )
    } catch {
      case e : Exception =>
        AppLogger.error(
          e,
          s"${headerMessage} Delete failed on [id:$entityId, entity: $entity]"
        )
    }

    getEmptyResponseForInFuture(actionType)
  }

  def addOrUpdateAsync(
    entityId : TKey,
    entity   : TRow
  ) : Future[RepositoryOperationResult[TRow, TKey]] = {
    if (isExists(entityId)) {
      // update
      return updateAsync(entityId, entity)
    }

    // add
    addAsync(entity)
  }

  def addAsync(entity : TRow) : Future[RepositoryOperationResult[TRow, TKey]] = {
    val actionType = DatabaseActionType.Create

    try {
      val action = getAddAction(entity)

      return this.saveAsync(dbAction = action, DatabaseActionType.Create)
    } catch {
      case e : Exception =>
        AppLogger.error(e, s"${headerMessage} Add failed on [entity: $entity]")
    }

    getEmptyResponseForInFuture(actionType)
  }

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
    entityId : TKey,
    entity   : TRow
  ) : Future[RepositoryOperationResult[TRow, TKey]] = {
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

  def deleteEntitiesAsync(
    entities : Iterable[TKey]
  ) : Iterable[Future[RepositoryOperationResult[TRow, TKey]]] = {
    if (entities == null || entities.isEmpty) {
      AppLogger.info(s"${headerMessage} No items passed for multiple deleting.")

      return null
    }

    entities.map(entity => this.deleteAsync(entity))
  }
}
