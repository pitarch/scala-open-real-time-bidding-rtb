package shared.com.ortb.model.wrappers.http

import play.api.mvc.Headers
import shared.com.ortb.model.wrappers.persistent.EntityWrapperWithOptions

//noinspection DuplicatedCode
case class HttpFailedExceptionActionWrapper[TRow, TKey](
  exception : Option[Exception],
  additionalMessage : Option[String] = None,

  methodName : Option[String] = None,
  lineNumber : Option[Int] = None,
  entityWrapper : Option[EntityWrapperWithOptions[TRow, TKey]] = None,
  controllerGenericActionWrapper : ControllerGenericActionWrapper,
  headers : Option[Seq[Headers]] = None
) extends HttpActionWrapperBase[TRow, TKey](
  methodName = methodName,
  lineNumber = lineNumber,
  entityWrapper = entityWrapper,
  controllerGenericActionWrapper = controllerGenericActionWrapper,
  headers = headers
)
