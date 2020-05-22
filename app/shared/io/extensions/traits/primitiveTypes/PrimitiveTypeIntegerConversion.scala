package shared.io.extensions.traits.primitiveTypes

trait PrimitiveTypeIntegerConversion {
  val i : Int

  def toBoolString : String = toBoolean.toString

  def toBoolean : Boolean = if (i == 0) false else true
}
