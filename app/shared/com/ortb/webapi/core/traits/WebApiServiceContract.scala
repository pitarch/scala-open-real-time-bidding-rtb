package shared.com.ortb.webapi.core.traits

import services.core.AbstractBasicPersistentService

trait WebApiServiceContract[TTable, TRow, TKey] {
  val service : AbstractBasicPersistentService[TTable, TRow, TKey]
}
