package shared.io.helpers.traits

import shared.com.ortb.manager.traits.DefaultExecutionContextManagerConcreteImplementation
import shared.io.helpers.ClassTagHelperConcreteImplementation

import scala.concurrent.ExecutionContext

trait ClassTagHelperProperties {
  this : ClassTagHelperConcreteImplementation =>
  lazy val executionContextManager = new DefaultExecutionContextManagerConcreteImplementation
  lazy val defaultMemberPossibility : Int = 30

  implicit override def createDefaultContext() : ExecutionContext = executionContextManager.defaultExecutionContext
}
