package com.ortb.persistent.repositories.pattern

import com.ortb.manager.AppManager
import com.ortb.model.config.ConfigModel
import com.ortb.persistent.repositories.pattern.traits._
import com.ortb.persistent.schema.DatabaseSchema
import io.traits.FutureToRegular

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class RepositoryBase[TTable, TRow, TKey](appManager: AppManager)
    extends DatabaseSchema(appManager)
    with SingleRepositoryBase[TTable, TRow, TKey]
    with EntityResponseCreator[TTable, TRow, TKey]
    with DatabaseActionExecutor[TTable, TRow, TKey]
    with FutureToRegular {

  lazy protected implicit val executionContext: ExecutionContext =
    appManager.executionContextManager
      .createDefault()
      .prepare()

  /**
    * default timeout from config, if < 0 then infinite
    */
  lazy protected implicit val defaultTimeout: Duration =
    if (config.defaultTimeout < 0) Duration.Inf
    else config.defaultTimeout.seconds

  lazy protected val config: ConfigModel = appManager.config
  lazy protected val headerMessage: String = s"[$tableName] ->"
}