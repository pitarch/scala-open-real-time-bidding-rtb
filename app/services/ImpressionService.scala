package services

import com.google.inject.Inject
import services.core.AbstractBasicPersistentService
import shared.com.ortb.persistent.repositories._
import shared.com.ortb.persistent.schema.Tables._

class ImpressionService @Inject()(
  injectedRepository : ImpressionRepository)
  extends AbstractBasicPersistentService[Impression, ImpressionRow, Int] {

  val repository = injectedRepository
}
