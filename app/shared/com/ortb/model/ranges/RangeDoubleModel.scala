package shared.com.ortb.model.ranges

import scala.util.Random

case class RangeDoubleModel(
  start : Double,
  end : Double
) extends RangeModel[Double] {
  /**
   * Get a static random number inclusive of start and exclusive of end
   */
  lazy override val staticRandomInBetweenRange : Double = randomInBetweenRange

  /**
   * Get a random number inclusive of start and exclusive of end
   */
  override def randomInBetweenRange : Double = Random.between(
    start,
    end)
}
