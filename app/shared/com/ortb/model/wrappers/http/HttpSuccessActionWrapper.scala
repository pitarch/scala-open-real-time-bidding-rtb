package shared.com.ortb.model.wrappers.http

import shared.com.ortb.enumeration.DatabaseActionType.DatabaseActionType
import shared.com.ortb.enumeration.HttpActionWrapperType.HttpActionWrapperType
import shared.com.ortb.model.wrappers.persistent.EntityWrapperWithOptions

//noinspection DuplicatedCode
case class HttpSuccessActionWrapper[TRow, TKey](
  additionalMessage  : Option[String] = None,

  methodName         : Option[String] = None,
  lineNumber         : Option[Int] = None,
  resultType         : Option[HttpActionWrapperType] = None,
  entityWrapper      : Option[EntityWrapperWithOptions[TRow, TKey]] = None,
  rawBodyRequest     : Option[String] = None,
  databaseActionType : Option[DatabaseActionType] = None
) extends HttpActionWrapperBase[TRow, TKey](
  methodName = methodName,
  lineNumber = lineNumber,
  resultType = resultType,
  entityWrapper = entityWrapper,
  rawBodyRequest = rawBodyRequest,
  databaseActionType = databaseActionType
)