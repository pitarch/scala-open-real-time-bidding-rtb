package shared.com.ortb.model.config

case class ConfigModel(
  applicationName : String,
  displayVersion : String,
  server : ServerInfoModel,
  logConfiguration : LogConfigurationModel,
  author : String,
  databaseRelativePath : String,
  databaseGenerate : DatabaseGenerateConfigModel,
  defaultTimeout : Int,
  defaultParallelProcessing : Int
)
