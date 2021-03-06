package shared.com.ortb.model.results

import shared.io.extensions.TypeConvertExtensions._
import shared.com.ortb.model.attributes.GenericResponseAttributesModel
import shared.com.ortb.model.wrappers.persistent.EntityWrapperModel
import shared.io.helpers.{ EmptyValidateHelper, ReflectionHelper }

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{ universe => ru }

case class RepositoryOperationResultModel[TRow, TKey](
  attributes : Option[GenericResponseAttributesModel],
  data : Option[EntityWrapperModel[TRow, TKey]] = None) {
  lazy val row : TRow = data.get.entity
  lazy val id : TKey = data.get.entityId
  lazy val idOption : Option[TKey] = data.get.entityId.toSome
  lazy val hasItem : Boolean = EmptyValidateHelper.isDefined(data)

  def getIdAsInt : Option[Int] = {
    val tt : ru.TypeTag[Int] = typeTag[Int]
    getIdAs[Int](tt)
  }

  def getIdAsString : Option[String] = {
    val tt : ru.TypeTag[String] = typeTag[String]
    getIdAs[String](tt)
  }

  def getIdAs[T](implicit T : ru.TypeTag[T]) : Option[T] = {
    if (attributes.isEmpty) {
      return None
    }

    if (ReflectionHelper.isIntegerType[T]) {
      return Some(attributes.get.id.get.toInt.asInstanceOf[T])
    } else if (ReflectionHelper.isStringType[T]) {
      return Some(attributes.get.id.get.asInstanceOf[T])
    } else {
      throw new NotImplementedError("Type can be either string or integer for T")
    }
  }
}
