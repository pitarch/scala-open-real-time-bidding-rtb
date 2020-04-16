package com.ortb.model.results

import com.ortb.enumeration.DatabaseActionType.DatabaseActionType
import slick.dbio.DatabaseAction

case class RepositoryOperationResult[T, TKey](
  isSuccess : Boolean, entityId : Option[TKey], entity : Option[T], actionType : DatabaseActionType, message : String = null)
