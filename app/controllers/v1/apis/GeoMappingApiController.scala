package controllers.v1.apis

import controllers.webapi.core.AbstractRestWebApi
import javax.inject.Inject
import play.api.mvc._
import services.core.AbstractBasicPersistentService
import services._
import shared.com.ortb.persistent.schema.Tables._

class GeoMappingApiController @Inject()(
  injectedService : GeoMappingService,
  components : ControllerComponents)
  extends AbstractRestWebApi[Geomapping, GeomappingRow, Int] (components) {

  override val service = injectedService
}
