package shared.io.loggers

import shared.com.ortb.configuration.ConfigurationExpansion
import shared.com.ortb.constants.AppConstants
import shared.com.ortb.enumeration.LogLevelType.LogLevelType
import shared.com.ortb.enumeration.{ DatabaseActionType, LogLevelType }
import shared.com.ortb.manager.AppManager
import shared.com.ortb.model.logging.LogTraceModel
import shared.com.ortb.persistent.repositories.LogTraceRepository
import shared.com.ortb.persistent.schema.Tables._
import shared.io.helpers.{ EmptyValidateHelper, JodaDateTimeHelper, ToStringHelper }

trait DatabaseLogTracer extends ConfigurationExpansion {
  val appManager : AppManager
  protected val className : String
  protected lazy val logTraceRepository : LogTraceRepository = new LogTraceRepository(appManager)

  def trace(
    log : LogTraceModel,
    message : String = "LogTrace",
    logLevelType : LogLevelType = LogLevelType.DEBUG) : Unit = {
    var entityString : Option[String] = AppConstants.EmptyStringOption
    var requestString : Option[String] = AppConstants.EmptyStringOption

    try {
      if (EmptyValidateHelper.isDefined(log.entity)) {
        entityString = ToStringHelper.toStringOf(log.entity)
      }

      if (EmptyValidateHelper.hasAnyItem(log.entities)) {
        val prevString = entityString.get
        val entitiesString = ToStringHelper.toStringOfItems(log.entities).get
        entityString = Some(prevString + "\n" + entitiesString)
      }
    }
    catch {
      case e : Exception => AppLogger.error(e)
    }

    try {
      if (EmptyValidateHelper.isDefined(log.request)) {
        requestString = Some(log.request.get.toString)
      }
    }
    catch {
      case e : Exception => AppLogger.error(e)
    }

    val row = LogtraceRow(
      -1,
      Some(log.methodName),
      Some(className),
      requestString,
      Some(log.message),
      entityString,
      log.databaseTransactionType,
      JodaDateTimeHelper.nowUtcJavaInstant)

    if (logConfiguration.isPrintDuringLogDatabaseActionsToDatabase) {
      AppLogger.logAsJson(
        message,
        logLevelType = logLevelType,
        maybeModel = Some(row))
    }

    if (logConfiguration.isLogDatabaseActionsToDatabase) {
      val addAction = logTraceRepository.getAddAction(row)

      logTraceRepository.saveAsync(
        addAction,
        DatabaseActionType.Create)
    }
  }
}
