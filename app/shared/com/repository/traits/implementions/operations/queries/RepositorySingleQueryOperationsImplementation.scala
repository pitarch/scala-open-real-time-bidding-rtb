package shared.com.repository.traits.implementions.operations.queries

import shared.com.repository.RepositoryBase
import shared.com.repository.traits.operations.queries.RepositorySingleQueryOperations

import scala.concurrent.{Await, Future}

trait RepositorySingleQueryOperationsImplementation[TTable, TRow, TKey]
  extends RepositorySingleQueryOperations[TTable, TRow, TKey] {
  this : RepositoryBase[TTable, TRow, TKey] =>
  def isEmptyGivenEntity(entityId: Option[TKey],
    entity: Option[TRow]): Boolean =
    entityId.isEmpty || entity.isEmpty

  def getFirstOrDefault(rows: Future[Seq[TRow]]): Option[TRow] = {
    this.getFirstOrDefault(Await.result(rows, defaultTimeout))
  }

  def getFirstOrDefault(rows: Seq[TRow]): Option[TRow] = {
    if (rows != null && rows.nonEmpty) {
      return Some(rows.head)
    }

    None
  }
}