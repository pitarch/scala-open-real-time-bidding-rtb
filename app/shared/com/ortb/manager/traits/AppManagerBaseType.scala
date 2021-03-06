package shared.com.ortb.manager.traits

import shared.com.ortb.manager.ConfigurationManagerImplementation
import shared.com.ortb.constants.AppConstants
import shared.com.ortb.model.config.ConfigModel

trait AppManagerBaseType {
  println(s"Running Config Directory ${AppConstants.PathConstants.ConfigDefaultPath}")
  lazy val ConfigManager = new ConfigurationManagerImplementation
  lazy val config : ConfigModel = ConfigManager
    .getConfig(AppConstants.PathConstants.ConfigDefaultPath)
}
