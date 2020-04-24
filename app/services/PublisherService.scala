package services

import com.google.inject.Inject
import services.core.AbstractBasicPersistentService
import shared.com.ortb.persistent.repositories._
import shared.com.ortb.persistent.schema.Tables._
import shared.com.repository.RepositoryBase

class PublisherService @Inject()(
  injectedRepository : PublisherRepository)
  extends AbstractBasicPersistentService[Publisher, PublisherRow, Int] {

  val repository = injectedRepository
}
