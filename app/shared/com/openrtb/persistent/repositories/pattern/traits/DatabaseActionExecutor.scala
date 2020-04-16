package com.ortb.persistent.repositories.pattern.traits

import java.awt.dnd.InvalidDnDOperationException

import com.ortb.enumeration.DatabaseActionType.DatabaseActionType
import com.ortb.implicits.ImplicitsDefinitions.anyRefCaller
import com.ortb.model.results.RepositoryOperationResult
import com.ortb.persistent.repositories.pattern.RepositoryBase
import io.AppLogger
import slick.dbio._
import slick.sql._

import scala.concurrent.{Await, Future}

trait DatabaseActionExecutor[TTable, TRow, TKey] {
  this: RepositoryBase[TTable, TRow, TKey] =>

  def quickSave(
    dbAction: FixedSqlAction[Int, NoStream, Effect.Write],
    actionType: DatabaseActionType
  ): RepositoryOperationResult[TRow, TKey] = {
    try {
      val result = db
        .run(dbAction)
        .map(
          (affectedRowsCount: Int) =>
            createResponseForAffectedRowCount(
              affectedRow = affectedRowsCount,
              entity = None,
              actionType = actionType
          )
        )

      return toRegular(result, defaultTimeout)
    } catch {
      case e: Exception =>
        AppLogger.error(e, s"${headerMessage} Failed at performing $actionType")
    }

    getEmptyResponseFor(actionType)
  }

  def saveAsync(
    entity: Option[TRow],
    dbAction: FixedSqlAction[Int, NoStream, Effect.Write],
    actionType: DatabaseActionType
  ): Future[RepositoryOperationResult[TRow, TKey]] = {
    try {
      return db
        .run(dbAction)
        .map(
          (affectedRowsCount: Int) =>
            createResponseForAffectedRowCount(
              affectedRowsCount,
              entity,
              actionType
          )
        )
    } catch {
      case e: Exception =>
        AppLogger.error(e, s"$headerMessage Failed at performing $actionType")
    }

    getEmptyResponseForInFuture(actionType)
  }

  def saveAsync(
    dbAction: FixedSqlAction[TRow, NoStream, Effect.Write],
    actionType: DatabaseActionType
  ): Future[RepositoryOperationResult[TRow, TKey]] = {
    try {
      return db
        .run(dbAction)
        .map(
          (entity: TRow) =>
            this.createResponseForAffectedRow(
              affectedEntity = Some(entity),
              actionType = actionType,
              isSuccess = entity != null,
              affectedRowsCount = Some(1)
          )
        )
    } catch {
      case e: Exception =>
        AppLogger.error(e, s"${headerMessage} Failed at performing $actionType")
    }

    getEmptyResponseForInFuture(actionType)
  }

  /**
    * Runs db.run(..) and returns the non async result.
    *
    * @param dbAction
    * @tparam T
    *
    * @return
    */
  def run[T >: Null <: AnyRef](dbAction: T): Seq[TRow] = {
    Await.result(this.runAsync(dbAction), defaultTimeout)
  }

  /**
    * runs db.run(..)
    *
    * @param dbAction
    * @tparam T
    *
    * @return
    */
  def runAsync[T >: Null <: AnyRef](dbAction: T): Future[Seq[TRow]] = {
    try {
      val results = getRunResult(dbAction).get
      AppLogger.logEntities(isLogQueries, results)
      return results
    } catch {
      case e: Exception => AppLogger.error(e)
    }

    null
  }

  def getRunResult[T >: Null <: AnyRef](
    dbAction: T
  ): Option[Future[Seq[TRow]]] = {
    dbAction match {
      case fixedSql: FixedSqlAction[Seq[TRow], _, _] =>
        Some(db.run(fixedSql))
      case fixedSqlStreaming: FixedSqlStreamingAction[Seq[TRow], NoStream, Effect.All] =>
        Some(db.run(fixedSqlStreaming))
      case sqlStreaming: SqlStreamingAction[Seq[TRow], NoStream, Effect.All] =>
        Some(db.run(sqlStreaming))
      case dbAction2: DBIOAction[_, _, _] =>
        val x = db.call("run", dbAction2).asInstanceOf[Future[Seq[TRow]]];
        Some(x)
      case _ =>
        throw new InvalidDnDOperationException(
          s"${headerMessage} Invalid operation for runAsync. Operation $dbAction"
        )
    }
  }
}