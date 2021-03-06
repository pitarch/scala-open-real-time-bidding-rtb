package services

import com.google.inject.Inject
import services.core.AbstractBasicPersistentService
import shared.com.ortb.persistent.repositories._
import shared.com.ortb.persistent.schema.Tables._
import shared.com.repository.RepositoryBase

class AuctionService @Inject()(
  injectedRepository : AuctionRepository)
  extends AbstractBasicPersistentService[Auction, AuctionRow, Int] {

  val repository : RepositoryBase[Auction, AuctionRow, Int] =
    injectedRepository
}
