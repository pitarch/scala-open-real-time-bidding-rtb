package shared.com.ortb.model

case class LogTraceModel(
  methodName : String,
  request : Option[Any] = None,
  message : String = "",
  entity : Option[Any] = None,
  entities : Option[Iterable[Any]] = None,
  databaseTransactionId : Option[Int] = None
)
