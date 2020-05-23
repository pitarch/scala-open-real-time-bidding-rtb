package shared.io.extensions.traits.primitiveTypes

import io.circe.Json
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import shapeless.Lazy
import shared.com.ortb.constants.AppConstants
import shared.com.ortb.enumeration.DatabaseActionType
import shared.com.ortb.persistent.repositories.LogTraceRepository
import shared.com.ortb.persistent.schema.Tables
import shared.io.extensions.TypeConvertExtensions._
import shared.io.helpers.{ EmptyValidateHelper, JodaDateTimeHelper, ReflectionHelper }
import shared.io.jsonParse.implementations.BasicJsonEncoderImplementation
import shared.io.jsonParse.traits.{ BasicJsonEncoder, GenericJsonParser }
import shared.io.loggers.AppLogger

trait TypeConvertGenericIterable[T] {
  protected val anyItems : Iterable[T]

  lazy val isEmpty : Boolean = !hasItem
  lazy val hasItem : Boolean = EmptyValidateHelper.hasAnyItemDirect(anyItems)
  lazy val toSome : Option[Iterable[T]] = Some(anyItems)
  lazy val toOption : Option[Iterable[T]] = Some(anyItems)
  lazy val toMaybe : Option[Iterable[T]] = Some(anyItems)
  lazy val toCsv : String = anyItems.mkString(",")
  lazy val logTraceRepository : LogTraceRepository = AppConstants.repositories.logTraceRepository

  def toJoinString(separator : String) : String = anyItems.mkString(separator)

  def toJoinString(start : String, separator : String, end : String) : String =
    anyItems.mkString(start, separator, end)

  def toJsons(
               implicit decoder : Lazy[DerivedDecoder[T]],
               encoder : Lazy[DerivedAsObjectEncoder[T]]) : Option[Iterable[Json]] = {
    throwOnNullOrNoneOrNil()
    jsonGenericParser(decoder, encoder).fromModelsToJsonObjects(toSome)
  }

  def toJsonString(implicit decoder : Lazy[DerivedDecoder[T]],
                   encoder : Lazy[DerivedAsObjectEncoder[T]]) : String = {
    throwOnNullOrNoneOrNil()
    val mayJsonString = jsonGenericParser(decoder, encoder).fromModelsToJsonString(toSome)
    mayJsonString.getDefinedString
  }

  def jsonGenericParser(
                         implicit decoder : Lazy[DerivedDecoder[T]],
                         encoder : Lazy[DerivedAsObjectEncoder[T]]) : GenericJsonParser[T] =
    basicJsonEncoder(decoder, encoder).getJsonGenericParser

  def basicJsonEncoder(
                        implicit decoder : Lazy[DerivedDecoder[T]],
                        encoder : Lazy[DerivedAsObjectEncoder[T]]) : BasicJsonEncoder[T] =
    new BasicJsonEncoderImplementation[T]()(decoder, encoder)

  private def throwOnNullOrNoneOrNil() : Unit = {
    EmptyValidateHelper.throwOnNullOrNoneOrNil(
      anyItems,
      Some(s"Undefined object given for json parsing."))
  }

  def logAsJson(message : String = "")(
    implicit decoder : Lazy[DerivedDecoder[T]],
    encoder : Lazy[DerivedAsObjectEncoder[T]]) : Unit = {
    val json = toPrettyJsonString
    AppLogger.log(json, message)
  }

  def toPrettyJsonString(
                          implicit decoder : Lazy[DerivedDecoder[T]],
                          encoder : Lazy[DerivedAsObjectEncoder[T]]) : String = {
    throwOnNullOrNoneOrNil()
    val maybeJson = jsonGenericParser(decoder, encoder).toJsonStringPrettyFormatForModels(
      toSome)

    maybeJson.getDefinedString
  }

  def logToDatabaseAsJson(
    methodName: String,
    databaseActionType: DatabaseActionType = DatabaseActionType.None,
    request: String = "",
    message : String = "")(
    implicit decoder : Lazy[DerivedDecoder[T]],
    encoder : Lazy[DerivedAsObjectEncoder[T]]) : Unit = {
    val json = toPrettyJsonString
val typeName = ReflectionHelper.getTypeName(anyItems.headOption)
    val logTrace = new Tables.LogtraceRow(
      AppConstants.NewRecordIntId,
      methodName,
      typeName,
      request,
      message,
      json,
      databaseActionType.value, JodaDateTimeHelper.)

    logTraceRepository.addAsync()
    AppLogger.log(json, message)
  }
}
