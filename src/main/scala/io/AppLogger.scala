package io

import com.ortb.model.error.FileErrorModel
import io.sentry.Sentry
import org.apache.logging.log4j.{LogManager, Logger}

object AppLogger {
  def methodName(): String = {
    val stack = Thread.currentThread().getStackTrace()(3);
    val message = s"${stack.getFileName}.${stack.getClassName}.${stack.getMethodName}() [Line: ${stack.getLineNumber}] ";
    return message;
  }

  val header = "[Open RTB]"
  val log: Logger = LogManager.getLogger(header)

  def info(msg: String = ""): Unit = {
    if (msg.length > 0) {
      val message = String.format("INFO : (%s) - %s", methodName(), msg);
      log.info(message);
      Sentry.getContext.addTag("level", "info");
      Sentry.capture(message);
    } else {
      log.info("");
    }
  }

  def debug(msg: String = ""): Unit = {
    if (msg.length > 0) {
      val message = String.format("DEBUG : (%s) - %s", methodName(), msg)
      Sentry.getContext.addTag("level", "debug");
      log.debug(message);
      Sentry.capture(message);
    } else {
      log.debug("");
    }
  }

  def warn(msg: String = ""): Unit = {
    if (msg.length > 0) {
      val message = String.format("(%s) - %s", methodName(), msg);
      Sentry.getContext.addTag("level", "warn");
      log.warn(message);
      Sentry.capture(message);
    } else {
      log.warn("")
    }
  }

  def error(msg: String = ""): Unit = {
    if (msg.length > 0) {
      val message = String.format("ERROR : (%s) - %s", methodName(), msg)
      Sentry.getContext.addTag("level", "error");
      log.error(message)
      Sentry.capture(message);
    } else {
      log.error("")
    }
  }

  def getFileErrorMessage(fileError: FileErrorModel): String = {
    val newLine = "\n <br >"
    s"Error Title: ${fileError.title}, ${newLine}FilePath: ${fileError.filePath},${newLine}Cause: ${fileError.cause},${newLine}Content: ${fileError.content}"
  }

  def fileError(fileError: FileErrorModel): Unit = {
    val message = getFileErrorMessage(fileError);
    Sentry.getContext.addTag("level", "error");
    log.error(message)
    Sentry.capture(message);
  }

  def error(exception: Exception): Unit = {
    log.error(exception.toString)
    Sentry.capture(exception);
  }

  def title: Any = log.warn(header)
}
