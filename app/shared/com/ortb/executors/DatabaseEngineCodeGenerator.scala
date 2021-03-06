package shared.com.ortb.executors

import shared.com.ortb.executors.codegen.CustomSourceCodeGenerator
import shared.com.ortb.manager.AppManager
import shared.com.ortb.manager.traits.DefaultExecutionContextManager
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

object DatabaseEngineCodeGenerator extends App with DefaultExecutionContextManager {
  lazy val appManager = new AppManager
  lazy val config = appManager.config
  lazy val databaseGenerateConfig = config.databaseGenerate
  lazy val databaseProfile = databaseGenerateConfig.profile // "slick.jdbc.SQLiteProfile"
  lazy val jdbcDriver = databaseGenerateConfig.jdbcDriver // "org.sqlite.JDBC",
  // "jdbc:sqlite:D:\\PersonalWork\\Github\\scala-rtb-example\\src\\main\\resources\\openRTBSample.db",
  lazy val url = databaseGenerateConfig.compiledDatabaseUrl // Database location path
  // "D:\\PersonalWork\\Github\\scala-rtb-example\\src\\main\\scala\\com\\ortb\\persistent\\schema",
  lazy val outputDir = databaseGenerateConfig.compiledOutputDir
  lazy val compilingPackage = databaseGenerateConfig.pkg
  lazy val db = appManager.db
  lazy val mTables = MTable.getTables(
    None,
    None,
    None,
    Some(Seq("TABLE", "VIEW")))

  val dbio = slick.jdbc.SQLiteProfile.createModel(
    Some(mTables))

  val model = db.run(dbio)
  println(model)
  
  val eventualSourceCodeGenerator : Future[SourceCodeGenerator] = model.map(model => new CustomSourceCodeGenerator(model))
  // val eventualSourceCodeGenerator : Future[SourceCodeGenerator] = model.map(model => new SourceCodeGenerator(model))
  val codegen : SourceCodeGenerator = Await.result(eventualSourceCodeGenerator, Duration.Inf)

  codegen.writeToFile(
    databaseProfile,
    outputDir,
    compilingPackage,
    "Tables",
    "Tables.scala")

  appManager.databaseEngine.closeDatabase()
}
