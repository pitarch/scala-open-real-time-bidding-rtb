package shared.com.repository.traits.implementions.operations.mutations

import shared.com.repository.RepositoryBase
import shared.com.repository.traits.operations.mutations.RepositoryOperations

trait RepositoryOperationsImplementation[TTable, TRow, TKey]
  extends RepositoryOperations[TTable, TRow, TKey]
    with RepositoryAddOperationsImplementation[TTable, TRow, TKey]
    with RepositoryUpdateOperationsImplementation[TTable, TRow, TKey]
    with RepositoryDeleteOperationsImplementation[TTable, TRow, TKey]
    with RepositoryAddOrUpdateOperationsImplementation[TTable, TRow, TKey] {
  this : RepositoryBase[TTable, TRow, TKey] =>
}
