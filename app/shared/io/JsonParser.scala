package io

import com.ortb.model.error.FileErrorModel

object JsonParser {
  def toObjectFromJSONPath[Type](
    path      : String,
    converter : (String) => Either[Error, Type]
  ) : Option[Type] = {
    try {
      val jsonContents = File.getContents(path)
      if (jsonContents.isEmpty) {
        return null
      }

      val convertedEitherItem = converter(jsonContents)
      val result              = convertedEitherItem.getOrElse(null)

      if (result != null) {
        return Some(result.asInstanceOf[Type])
      }
      else {
        val errorContext   = convertedEitherItem.left.getOrElse(null).toString
        val fileErrorModel = new FileErrorModel(
          title = s"Json Parsing Failed for : ${path}",
          cause = errorContext,
          filePath = path,
          content = jsonContents);

        val errorMessage = AppLogger.getFileErrorMessage(fileErrorModel)
        throw new Exception(errorMessage)
      }
    }
    catch {
      case e : Exception =>
        AppLogger.error(e)
    }

    null
  }
}