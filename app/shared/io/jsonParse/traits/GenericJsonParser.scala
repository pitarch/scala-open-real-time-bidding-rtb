package shared.io.jsonParse.traits

import com.fasterxml.jackson.databind.JsonNode
import io.circe.Json

import scala.collection.mutable.ArrayBuffer

trait GenericJsonParser[T] {
  def toJsonNodes(jsonString : Option[String]) : Option[ArrayBuffer[JsonNode]]

  def toModel(jsonString : Option[String]) : Option[T]

  def toModelDirect(jsonString : String) : T

  def toJsonNode(jsonString : Option[String]) : Option[JsonNode]

  def fromModelToJsonNode(model : Option[T]) : Option[JsonNode]

  def fromModelsToJsonNodes(models : Option[Iterable[T]]) : Option[Iterable[JsonNode]]

  def fromModelsToJsonString(models : Option[Iterable[T]]) : Option[String]

  def fromJsonStringToModels(jsonString : Option[String]) : Option[Iterable[T]]

  def toModels(jsonString : Option[String]) : Option[ArrayBuffer[T]]

  def toJsonObject(model : Option[T]) : Option[Json]

  def toJsonObjects(models : Option[Iterable[T]]) : Option[Iterable[Json]]

  /**
   * Usages 2 space json String format.
   * @param models
   * @return
   */
  def toJsonStringPrettyFormat(models : Option[Iterable[T]]) : Option[String]

  def toJsonString(model : Option[T]) : Option[String]

  def toJsonObjectDirect(model : T) : Json

  def toJsonStringDirect(model : T) : String

  def fromModelsToJsonObjects(model : Option[Iterable[T]]) : Option[Iterable[Json]]

  def fromJsonToJsonString(model : Option[Json]) : Option[String]

  /**
   *
   * @param models
   * @param additionalAnnotationForItems
   * @param isPrettyFormat if true then 2 space json format.
   * @return
   */
  def fromJsonsToJsonString(
    models : Option[Iterable[Json]],
    additionalAnnotationForItems : String = null,
    isPrettyFormat:Boolean = false) : Option[String]

  def toJsonString(
    entities : Iterable[T],
    additionalAnnotationForItems : String = null) : String

  def toLogStringForEntities(
    entities : Option[Iterable[T]]) : String

  def toLogStringForEntity(
    entity : Option[T]) : String
}
