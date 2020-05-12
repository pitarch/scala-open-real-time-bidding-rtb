package controllers.rtb.implementations

import play.api.mvc.{ AbstractController, Result }
import play.mvc.Http.MimeTypes

class RestWebApiOkJsonImplementation(controller : AbstractController) {
  def OkJson(jsonString : String) : Result = controller.Ok(jsonString).as(MimeTypes.JSON)
}