package shared.io.loggers.traits

import shared.com.ortb.enumeration.LogLevelType
import shared.io.loggers.AppLogger._

trait DebugLogger {
  def debug(
    msg1 : String,
    msg2 : String = "",
    stackIndex : Int = defaultStackIndex,
    isPrintStack : Boolean = false) : Unit = {
    val msgCompiled = if (msg2.isEmpty) msg1 else s"${ msg1 } - ${ msg2 }"
    val methodNameDisplay = getMethodNameDisplayWrapper(stackIndex)

    val message = s"${
      LogLevelType.DEBUG
    } :$methodNameDisplay ${ msgCompiled }"

    additionalLogging(
      message = message,
      logLevelType = LogLevelType.DEBUG,
      stackIndex = stackIndex,
      isPrintStack = isPrintStack
    )
  }
}
