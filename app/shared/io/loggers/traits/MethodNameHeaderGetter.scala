package shared.io.loggers.traits

import shared.com.ortb.constants.AppConstants
import shared.io.helpers.EmptyValidateHelper
import shared.io.loggers.AppLogger

trait MethodNameHeaderGetter extends
  StackTraceInfoDisplayGetter {
  this : AppLogger.type =>

  def getMethodNameHeader(stackIndex : Int) : String = {
    val stacks = Thread.currentThread().getStackTrace
    val len = stacks.length
    val lastIndex = len - 2

    if (lastIndex <= stackIndex) {
      return getStackTraceInfo(stacks(stackIndex)) + getStackTraceInfo(stacks(lastIndex))
    }

    if (stacks.nonEmpty) {
      return getStackTraceInfo(stacks(lastIndex))
    }

    ""
  }

  private def getMethodNameHeaderForIndexes(
    indexes : Int*
  ) : String = {
    if (isPrintMethodName) {
      return indexes.map(index => getMethodNameHeader(index))
                    .mkString(AppConstants.HyphenRightAngel)
    }

    ""
  }

  def getMethodNameDisplayWrapper(
    stackIndex : Int
  ) : String = {
    val methodNameHeaders = getMethodNameHeaderForIndexes(stackIndex, stackIndex + 1)
    getMethodNameForDisplay(methodNameHeaders)
  }

  private def getMethodNameForDisplay(
    methodName : String = ""
  ) : String = {
    if (EmptyValidateHelper.isEmptyString(methodName)) {
      return " - "
    }
    s" ($methodName) - "
  }
}
