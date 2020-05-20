package shared.com.ortb.demadSidePlatforms.traits.properties

import controllers.rtb.traits.properties.ServiceControllerCorePropertiesContracts
import shared.com.ortb.model.config.{ DemandSidePlatformConfigurationModel, RangeModel, ServiceModel }
import shared.com.ortb.persistent.Repositories
import shared.com.ortb.persistent.repositories.DemandSidePlatformRepository
import shared.com.ortb.persistent.schema.Tables
import shared.io.loggers.{ DatabaseLogTracer, DatabaseLogTracerImplementation }

import scala.util.Random

trait DemandSidePlatformCorePropertiesContracts
  extends ServiceControllerCorePropertiesContracts
    with BiddingDefaultProperties
    with DemandSidePlatformBiddingProperties {
  lazy val randomNumberIncrementerGuessRange : RangeModel =
    demandSidePlatformConfiguration.randomNumberIncrementerGuessRange

  lazy override val databaseLogger : DatabaseLogTracer = new DatabaseLogTracerImplementation(
    appManager,
    this.getClass.getName)

  lazy override val currentServiceModel : ServiceModel =
    services.demandSidePlatForms(demandSideId - 1)

  lazy val demandSidePlatformRepository : DemandSidePlatformRepository =
    repositories.demandSidePlatformRepository
  lazy val demandSidePlatformEntity : Option[Tables.DemandsideplatformRow] = demandSidePlatformRepository
    .getById(demandSideId)
  lazy val demandSidePlatformJson : Option[String] = demandSidePlatformRepository
    .fromEntityToJson(demandSidePlatformEntity)

  lazy val demandSidePlatformConfiguration : DemandSidePlatformConfigurationModel =
    config.server.demandSidePlatformConfiguration
  val repositories : Repositories
}
