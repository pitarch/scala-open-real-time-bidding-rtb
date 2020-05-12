package shared.com.ortb.executors

import shared.com.ortb.manager.AppManager
import shared.com.ortb.persistent.Repositories
import shared.com.ortb.persistent.schema.Tables._
import shared.io.helpers.{ JodaDateTimeHelper, ReflectionHelper }
import shared.io.loggers.AppLogger

object Application {
  def main(args : Array[String]) : Unit = {
    val appManager = new AppManager
    println(appManager.config)

    val repos = new Repositories(appManager)
    val campaignsResponse = repos.campaignRepository.getAllAsResponse
    campaignsResponse.logResults()

    val bidResponseRepository = repos
      .bidResponseRepository
    // println(nameOf(bidResponseRepository))

    val allRows = bidResponseRepository.getAll
    AppLogger.logEntities( allRows, "all rows")
    //    val toAllDates = allRows.map(w => {
    //      w.cr2.get..toString
    //    })

    //    AppLogger.logEntitiesNonFuture(true, toAllDates, "toAllDates")

    val row = BidresponseRow(
      -1,
      createddatetimestamp = JodaDateTimeHelper.nowUtcJavaInstant)
    val response = bidResponseRepository
      .add(row)

    bidResponseRepository
      .add(row)

    val res = response.data.get

    val w = ReflectionHelper.classTagHelper.getMembersInfo[BidresponseRow]
    val w2 = ReflectionHelper.classTagHelper.getMembersInfoForT(Some(response))

    val we  = ReflectionHelper.getCaseClassInfoModel(res)

    // AppLogger.debug(response.toString)
  }
}
