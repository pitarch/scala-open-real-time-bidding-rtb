package shared.io.helpers

import java.nio.file._
import java.util

import shared.io.helpers.traits.file.ExecuteInputOutputAction
import shared.io.loggers.AppLogger

import scala.io.{ BufferedSource, Source }

object FileHelper extends ExecuteInputOutputAction {
  /**
   * Input relative path from resource and get the contents of the file.
   *
   * @param relativePathToResource : file system (relative path) in resource directory
   *
   * @return contents of the file.
   */
  def getContentsFromResources(relativePathToResource : String) : String = {
    val actualPath = PathHelper.getResourceFileAbsolutePath(relativePathToResource)

    getContents(actualPath)
  }

  def getContentsFromResourcesPaths(relativePathsToResource : String*) : String = {
    val actualPath = PathHelper.getResourceFileAbsolutePathSequence(relativePathsToResource)
    AppLogger.debug(actualPath)

    getContents(actualPath)
  }

  /**
   * return given path's content if file exist or else returns empty string ("").
   *
   * @param path : file system path
   *
   * @return content in string format.
   */
  def getContents(path : String) : String = {
    val givenPath = Paths.get(path)

    def action() = Files.readString(givenPath)

    executeInputOutputAction(givenPath, action, "")
  }

  /**
   * (In memory operation) Input relative path from resource and get the lines list of the file.
   *
   * @param relativePathToResource : file system (relative path) in resource directory
   *
   * @return contents of the file.
   */
  def getLinesFromResources(relativePathToResource : String) : util.List[String] = {
    val actualPath = PathHelper.getResourceFileAbsolutePath(relativePathToResource)

    getLines(actualPath)
  }

  /**
   * return given path's content if file exist or else returns null.
   *
   * @param path : file system path
   *
   * @return contents in Java List
   */
  def getLines(path : String) : util.List[String] = {
    val givenPath = Paths.get(path)

    def action() = Files.readAllLines(givenPath)

    executeInputOutputAction(givenPath, action, null)
  }

  override def executeInputOutputAction[ReturnType](
    path : Path,
    performingAction : () => ReturnType,
    emptyResult : ReturnType
  ) : ReturnType = {
    try {
      if (Files.exists(path)) {
        return performingAction()
      }
    }
    catch {
      case e : Exception =>
        AppLogger.error(e)
    }

    AppLogger.debug(s"${ path } doesn't exist or cannot (something went wrong) read from file system.")
    emptyResult
  }

  /**
   * (In memory operation) Input relative path from resource and get the lines list of the file.
   *
   * @param relativePathToResource : file system (relative path) in resource directory
   *
   * @return contents of the file.
   */
  def getBufferedSourceFromResources(relativePathToResource : String) : BufferedSource = {
    val actualPath = PathHelper.getResourceFileAbsolutePath(relativePathToResource)

    getBufferedSource(actualPath)
  }

  /**
   * return buffered source using Source.fromFile
   *
   * @param path : file system path
   *
   * @return content in string format.
   */
  def getBufferedSource(path : String) : BufferedSource = {
    val givenPath = Paths.get(path)

    def action() = Source.fromFile(path)

    executeInputOutputAction(givenPath, action, null)
  }
}
