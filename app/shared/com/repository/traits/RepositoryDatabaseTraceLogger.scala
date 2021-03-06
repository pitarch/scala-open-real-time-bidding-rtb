package shared.com.repository.traits

import shared.io.extensions.TypeConvertExtensions._
import shared.com.ortb.enumeration.{ DatabaseActionType, LogLevelType }
import shared.com.ortb.enumeration.LogLevelType.LogLevelType
import shared.com.ortb.manager.traits.DefaultExecutionContextManager
import shared.com.ortb.model.logging.LogTraceModel
import shared.com.ortb.model.results.{ RepositoryOperationResultModel, RepositoryOperationResultsModel }
import shared.io.loggers.{ AppLogger, DatabaseLogTracerImplementation }

import scala.concurrent.Future
import scala.util.{ Failure, Success }

trait RepositoryDatabaseTraceLogger[TRow, TKey] extends DefaultExecutionContextManager {
  val databaseLogger : DatabaseLogTracerImplementation
  protected val headerMessage : String

  def tracePrePost(
    isLogQueries : Boolean,
    methodName : String,
    preRow : Option[TRow],
    completeRow : Option[TRow],
    databaseActionType : DatabaseActionType,
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    if (!isLogQueries) {
      return
    }

    val logModel = LogTraceModel(
      methodName,
      Some(preRow),
      s"[$headerMessage] [${ databaseActionType.toString }]",
      entity = Some(completeRow))

    databaseLogger.trace(logModel, logLevelType = logLevelType)
  }

  def trace(
    isLogQueries : Boolean,
    methodName : String,
    resultModel : Option[RepositoryOperationResultModel[TRow, TKey]],
    databaseActionType : DatabaseActionType,
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    if (!isLogQueries) {
      return
    }

    val logModel = LogTraceModel(
      methodName,
      resultModel,
      resultModel.get.attributes.get.message,
      entity = resultModel.get.data,
      entities = None,
      databaseTransactionType = databaseActionType.value.toSome)

    databaseLogger.trace(logModel, logLevelType = logLevelType)
  }

  def traceResults(
    isLogQueries : Boolean,
    methodName : String,
    resultsModel : Option[RepositoryOperationResultsModel[TRow, TKey]],
    databaseActionType : DatabaseActionType,
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    if (!isLogQueries) {
      return
    }

    val logModel = LogTraceModel(
      methodName,
      Some(resultsModel),
      resultsModel.get.attributes.get.message,
      entity = None,
      entities = Some(resultsModel.get.data),
      databaseTransactionType = databaseActionType.value.toSome)

    databaseLogger.trace(logModel, logLevelType = logLevelType)
  }

  def traceFutureResult(
    isLogQueries : Boolean,
    methodName : String,
    resultModel : Option[Future[RepositoryOperationResultModel[TRow, TKey]]],
    databaseActionType : DatabaseActionType,
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    if (resultModel.isEmpty) {
      return
    }

    resultModel.get.onComplete {
      case Success(value) =>
        trace(
          isLogQueries,
          methodName,
          Some(value),
          databaseActionType, logLevelType)

      case Failure(value) =>
        AppLogger.error(value.toString)
    }
  }

  def traceFutureResults(
    isLogQueries : Boolean,
    methodName : String,
    resultsModel : Option[Future[RepositoryOperationResultsModel[TRow, TKey]]],
    databaseActionType : DatabaseActionType,
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    if (resultsModel.isEmpty) {
      return
    }

    resultsModel.get.onComplete(w =>
      traceResults(
        isLogQueries,
        methodName,
        Some(w.get),
        databaseActionType, logLevelType))
  }
}
