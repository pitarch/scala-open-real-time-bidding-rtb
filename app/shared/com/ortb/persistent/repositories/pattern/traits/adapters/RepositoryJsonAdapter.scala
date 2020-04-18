package shared.com.ortb.persistent.repositories.pattern.traits.adapters

import io.circe.{Decoder, Encoder}
import shared.com.ortb.model.wrappers.persistent.EntityWrapperWithOptions
import shared.com.ortb.persistent.repositories.pattern.traits.RepositoryOperationsBase

trait RepositoryJsonAdapter[TTable, TRow, TKey]
  extends RepositoryOperationsBase[TRow] {

  def fromEntityToJson(entity : Option[TRow])
    (implicit encoder : Encoder[TRow])
  : Option[String]

  def fromEntitiesToJson(entities : Option[Iterable[TRow]])
    (implicit encoder : Encoder[Iterable[TRow]])
  : Option[String]

  def fromJsonToEntityWrapper(jsonContent : Option[String])(
    implicit decoder : Decoder[TRow])
  : Option[EntityWrapperWithOptions[TRow, TKey]]

  def fromJsonToEntitiesWrapper(jsonContent : Option[String])(
    implicit decoder : Decoder[Iterable[TRow]])
  : Option[Iterable[EntityWrapperWithOptions[TRow, TKey]]]
}
