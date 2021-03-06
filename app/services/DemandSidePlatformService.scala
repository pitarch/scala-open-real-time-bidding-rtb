package services

import com.google.inject.Inject
import services.core.AbstractBasicPersistentService
import shared.com.ortb.persistent.repositories._
import shared.com.ortb.persistent.schema.Tables._

class DemandSidePlatformService @Inject()(
  injectedRepository : DemandSidePlatformRepository)
  extends AbstractBasicPersistentService[Demandsideplatform, DemandsideplatformRow, Int] {

  val repository = injectedRepository
}
