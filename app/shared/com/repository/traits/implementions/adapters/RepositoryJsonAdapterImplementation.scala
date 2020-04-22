package shared.com.repository.traits.implementions.adapters

import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import shared.com.ortb.adapters.BasicAdapterImplementation
import shared.com.ortb.model.wrappers.persistent.EntityWrapperWithOptions
import shared.com.repository.RepositoryBase
import shared.com.repository.traits.adapters.RepositoryJsonAdapter
import shared.io.helpers.EmptyValidateHelper
import shared.io.loggers.AppLogger

trait RepositoryJsonAdapterImplementation[TTable, TRow, TKey]
    extends BasicAdapterImplementation
    with RepositoryJsonAdapter[TTable, TRow, TKey] {
  this: RepositoryBase[TTable, TRow, TKey] =>

  //noinspection DuplicatedCode
  def fromEntityToJson(entity: Option[TRow]): Option[String] =
    convertItemTo[TRow, String](entity, _ => {
      try {
        val encoder = this.encoders.defaultEncoder
        return Some(entity.get.asJson(encoder).spaces2)
      } catch {
        case e: Exception => AppLogger.error(e)
      }

      None
    })

  //noinspection DuplicatedCode
  def fromEntitiesToJson(entities: Option[List[TRow]]): Option[String] = {
    convertItemTo[List[TRow], String](
      entities,
      _ => {
        try {
          val encoder = this.encoders.defaultListEncoder
          return Some(entities.get.asJson(encoder).spaces2)
        } catch {
          case e: Exception => AppLogger.error(e)
        }

        None
      }
    )
  }

  def fromJsonToEntityWrapper(jsonContent: Option[String])
    : Option[EntityWrapperWithOptions[TRow, TKey]] = {
    def converter(jsonAsString: Option[String])
      : Option[EntityWrapperWithOptions[TRow, TKey]] = {
      try {
        val decoder = this.encoders.defaultDecoder
        val possibleEntity = decode[TRow](jsonAsString.get)(decoder)
          .getOrElse(null)

        if (possibleEntity != null) {
          val entity = possibleEntity.asInstanceOf[TRow]

          return toEntityWrapperWithOptions(Some(entity))
        }
      } catch {
        case e: Exception => AppLogger.error(e, jsonAsString.get)
      }

      None
    }

    convertItemTo(jsonContent, converter)
  }

  override def fromJsonToEntitiesWrapper(jsonContent: Option[String])
    : Option[List[EntityWrapperWithOptions[TRow, TKey]]] = {
    val isEmpty = EmptyValidateHelper.isEmpty(jsonContent)
    if (isEmpty) {
      return None
    }

    try {
      val decoder = this.encoders.defaultListDecoder
      val possibleEntities = decode[List[TRow]](jsonContent.get)(decoder)
        .getOrElse(null)

      if (possibleEntities != null) {
        val entities = possibleEntities.asInstanceOf[List[TRow]]

        return toEntitiesWrapperWithOptions(Some(entities))
      }
    } catch {
      case e: Exception => AppLogger.error(e, jsonContent.get)
    }

    None
  }
}
