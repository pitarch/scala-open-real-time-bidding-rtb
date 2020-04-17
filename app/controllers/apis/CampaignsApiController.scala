package controllers.apis

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import javax.inject.Inject
import play.api.mvc.{Action, _}
import services.CampaignService
import shared.com.ortb.model.results.RepositoryOperationResult
import shared.com.ortb.persistent.schema.Tables._
import shared.io.logger.AppLogger

trait WebApi[TTable, TRow, TKey] {
  def getAll : Action[AnyContent]

  def byId(id : TKey) : Action[AnyContent]

  def add() : Action[AnyContent]

  def update(id : TKey) : Action[AnyContent]

  def delete(id    : TKey) : Action[AnyContent]

  //
  //  def addEntities() : Action[AnyContent]
  //
  //  def addEntitiesBySinge() : Action[AnyContent]

}

class CampaignsApiController @Inject()(
  campaignService : CampaignService,
  components      : ControllerComponents)
  extends AbstractController(components) with
    WebApi[Campaign, CampaignRow, Int] {

  override def getAll : Action[AnyContent] = Action { implicit request =>
    val campaigns = campaignService.getAll
    val json = campaigns.asJson.spaces2
    Ok(json)
  }

  override def byId(id : Int) : Action[AnyContent] = Action { implicit request =>
    val campaign = campaignService.getById(id)
    val json = campaign.get.asJson.spaces2
    Ok(json)
  }

  override def add() : Action[AnyContent] = Action { implicit request =>
    try {
      val body = request.body.asText.getOrElse("")
      val entity = decode[CampaignRow](body).getOrElse(null)
      val response : RepositoryOperationResult[CampaignRow, Int] = campaignService.add(entity)

      if (response.isSuccess) {
        val json = response.entity.get.asJson.spaces2
        Ok(json)
      }
      else {
        BadRequest(s"Failed to create. (raw body: $body)")
      }
    } catch {
      case e : Exception => AppLogger.error(e)
        BadRequest(e.toString)
    }
  }

  override def update(id : Int) : Action[AnyContent] = Action {
    implicit request =>
      try {
        val body = request.body.asText.getOrElse("")
        val entity = decode[CampaignRow](body).getOrElse(null)
        AppLogger.debug("put - update")
        AppLogger.logEntityNonFuture(
          isExecute = true,
          entity = Some(entity),
          additionalMessage = body)

        if (entity == null) {
          BadRequest(s"Entity conversion failed for given (source received:$body).")
        }

        val response = campaignService.update(id, entity)

        if (!response.isSuccess) {
          BadRequest(s"Update request failed (source received:$body).")
        }
        else {
          val e2 = response.entity.get
          val json = e2.asJson.spaces2

          AppLogger.logEntityNonFuture(
            isExecute = true,
            entity = Some(e2),
            additionalMessage = json)

          Ok(json)
        }

      } catch {
        case e : Exception => AppLogger.error(e)
          BadRequest(e.toString)
      }
  }

  override def delete(id : Int) : Action[AnyContent] = Action {
    implicit request =>
      val response = campaignService.delete(id)
      val json = response.entity.get.asJson.spaces2
      Ok(json)
  }

  //
  //  override def addEntities(
  //    entities : Iterable[Tables.CampaignRow]) : Action[AnyContent] = Action { implicit request =>
  //    val response = campaignService.addEntities(entities)
  //                                  .map(w => w.entity).toArray
  //    val json = response.asJson.spaces2
  //    Ok(json)
  //  }
  //
  //  override def addEntitiesBySinge(
  //    entity   : Tables.CampaignRow,
  //    addTimes : Int) : Action[AnyContent] = Action { implicit request =>
  //    val response = campaignService.addEntities(entity, addTimes)
  //                                  .map(w => w.entity).toArray
  //
  //    val json = response.asJson.spaces2
  //    Ok(json)
  //  }
}
