package shared.com.ortb.persistent.repositories.pattern.traits.adapters

import shared.com.ortb.model.wrappers.persistent.{
  EntityWrapper,
  EntityWrapperWithOptions
}
import shared.com.ortb.persistent.repositories.pattern.traits.RepositoryOperationsBase

trait RepositoryWrapperAdapter[TTable, TRow, TKey]
    extends RepositoryOperationsBase[TRow] {
  def toEntityWrapper(item: Option[TRow]): Option[EntityWrapper[TRow, TKey]]

  def fromEntityWrapperToEntityWrapperWithOptions(
      item: Option[EntityWrapper[TRow, TKey]])
    : Option[EntityWrapperWithOptions[TRow, TKey]]

  def fromEntityWrapperWithOptionsToEntityWrapper(
    item: Option[EntityWrapperWithOptions[TRow, TKey]])
  : Option[EntityWrapper[TRow, TKey]]

  def toEntityWrapperWithOptions(
      item: Option[TRow]): Option[EntityWrapperWithOptions[TRow, TKey]]

  def toEntitiesWrapper(items: Option[Iterable[TRow]])
    : Option[Iterable[EntityWrapper[TRow, TKey]]]

  def toEntitiesWrapperWithOptions(items: Option[Iterable[TRow]])
    : Option[Iterable[EntityWrapperWithOptions[TRow, TKey]]]
}