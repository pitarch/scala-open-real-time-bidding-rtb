package shared.com.ortb.model

import java.lang.reflect.{ Method, Parameter, Type }

case class MethodWrapperModel(
  method : Method
)
  extends MemberWrapperModel(method) {
  lazy val parameters : Array[Parameter] = method.getParameters
  lazy val genericParameters : Array[Type] = method.getGenericParameterTypes
  lazy val returnType : Class[_] = method.getReturnType
  lazy val parameterCount : Int = method.getParameterCount
}
