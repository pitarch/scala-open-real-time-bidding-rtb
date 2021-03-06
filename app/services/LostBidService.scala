package services

import com.google.inject.Inject
import services.core.AbstractBasicPersistentService
import shared.com.ortb.persistent.repositories._
import shared.com.ortb.persistent.schema.Tables._

class LostBidService @Inject()(
  injectedRepository : LostBidRepository)
  extends AbstractBasicPersistentService[Lostbid, LostbidRow, Int] {

  val repository = injectedRepository
}
