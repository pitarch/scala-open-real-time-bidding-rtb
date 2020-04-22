package shared.com.ortb.constants

object AppConstants {

  /**
    * Dot(.) string.
    */
  lazy val Dot = "."

  /**
    * "configuration.json"
    */
  lazy val DefaultConfigFileNameWithExtension = "configuration.json"

  lazy val PathConstants = new PathConstants

  lazy val QueryStringNameConstants = new QueryStringNameConstants

  lazy val SentryDSN = "https://3540a18396eb4373b3c843b149c55f5d@sentry.io/5183951"

  lazy val DefaultPageSize = 100

  /**
    * ",\n <br >"
    */
  lazy val NewLineForSentry = ",\n <br >"

  /**
    * "-> "
    */
  lazy val HyphenRightAngel = "-> "

  /**
    * "localhost"
    */
  lazy val LocalHost = "localhost"

  lazy val DefaultParallelExecution = 3

  lazy val NoContent = "No content, no operation performed."
}
