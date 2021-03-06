package shared.com.ortb.demadSidePlatforms.traits.getters

import com.github.dwickern.macros.NameOf._
import shared.com.ortb.demadSidePlatforms.DemandSidePlatformBiddingAgent
import shared.com.ortb.model.auctionbid._
import shared.com.ortb.model.logging.LogTraceModel
import shared.com.ortb.model.results.DemandSidePlatformBiddingRequestWrapperModel
import shared.com.ortb.persistent.Repositories
import shared.com.ortb.persistent.schema.Tables
import shared.io.helpers.{ EmptyValidateHelper, NumberHelper }
import slick.jdbc.SQLiteProfile.api._

trait FailedBidsGetter {
  this : DemandSidePlatformBiddingAgent =>

  def getLastFailedDealsAsBidFailedInfoWithRows(
    request : DemandSidePlatformBiddingRequestWrapperModel,
    limit : Int = defaultLimit) : BidFailedInfoWithRowsModel = {
    lazy val methodName = nameOf(getLastFailedDealsAsBidFailedInfoWithRows _)
    val repositories = coreProperties.repositories
    val lostBids = repositories.lostBids

    val lostBidResults = getLostBids(
      limit,
      lostBids)

    val bidFailedInfoModel : BidFailedInfoModel = getBidFailedInfoModel(
      limit,
      lostBidResults)

    val bidFailedInfoWithRowsModel = BidFailedInfoWithRowsModel(
      attributes = bidFailedInfoModel,
      data = lostBidResults
    )

    val logTraceModel = LogTraceModel(
      methodName = methodName,
      request = Some(request),
      entity = Some(bidFailedInfoWithRowsModel))

    coreProperties
      .databaseLogger
      .trace(logTraceModel)

    bidFailedInfoWithRowsModel
  }

  private def getBidFailedInfoModel(
    limit : Int,
    lostBidResults : Seq[Tables.LostbidRow]) : BidFailedInfoModel = {
    if (EmptyValidateHelper.isItemsEmptyDirect(lostBidResults)) {
      return BidFailedInfoModel(
        lastLostBid = null,
        lastWinningBid = null,
        lastLosingPrice = 0,
        lastWiningPrice = 0,
        averageOfLosingPrices = 0,
        averageOfWiningPrices = 0,
        absoluteDifferenceOfAverageLosingAndWinningPrice = 0,
        absoluteDifferenceOfLosingAndWinningPrice = 0,
        staticIncrement = coreProperties.defaultIncrementNumber
      )
    }

    val length = lostBidResults.length
    val repositories = coreProperties.repositories
    val averageOfLosingPrice = lostBidResults.map(w => NumberHelper.getAsDouble(w.losingprice)).sum / length
    val lastLosingPrice = NumberHelper.getAsDouble(lostBidResults.head.losingprice)

    val winningPriceModel = getWinningPricesModel(limit, repositories)
    val averageOfWiningPrices = winningPriceModel.averageOfWiningPrices
    val lastWiningPrice = winningPriceModel.lastWinningPrice

    val absoluteDifferenceOfAverageLosingAndWinningPrice =
      Math.abs(averageOfWiningPrices - averageOfLosingPrice)
    val absoluteDifferenceOfLosingAndWinningPrice =
      Math.abs(lastWiningPrice - lastLosingPrice)

    val bidFailedInfoModel = BidFailedInfoModel(
      lastLostBid = lostBidResults.head,
      lastWinningBid = winningPriceModel.lastWinningBidRow,
      lastLosingPrice = lastLosingPrice,
      lastWiningPrice = lastWiningPrice,
      averageOfLosingPrices = averageOfLosingPrice,
      averageOfWiningPrices = averageOfWiningPrices,
      absoluteDifferenceOfAverageLosingAndWinningPrice = absoluteDifferenceOfAverageLosingAndWinningPrice,
      absoluteDifferenceOfLosingAndWinningPrice = absoluteDifferenceOfLosingAndWinningPrice,
      staticIncrement = coreProperties.defaultIncrementNumber
    )

    bidFailedInfoModel
  }

  def getLostBids(
    limit : Int,
    lostBids : TableQuery[Tables.Lostbid]) : Seq[Tables.LostbidRow] = {
    val repositories = coreProperties.repositories
    val demandSidePlatformId = coreProperties.demandSideId
    val lostBidsQuery = lostBids
      .filter(r => r.demandsideplatformid === demandSidePlatformId)
      .sortBy(_.lostbidid.desc)
      .take(limit)

    val lostBidsSqlProfileAction = lostBidsQuery
      .result

    val lostBidResults = repositories.lostBidRepository
      .run(lostBidsSqlProfileAction)

    lostBidResults
  }

  def getWinningPricesModel(
    limit : Int,
    repositories : Repositories) : WinningPricesModel = {
    val winningPriceInfoViewRepository = repositories.viewsRepositories
      .winningPriceInfoViewRepository
    val query = winningPriceInfoViewRepository
      .getAllQuery
      .filter(w => w.iswon === 1)
      .sortBy(_.impressionid.desc)
      .take(limit)
      .result

    val results = winningPriceInfoViewRepository.getResults(query)

    if (EmptyValidateHelper.isItemsEmptyDirect(results)) {
      return WinningPricesModel(
        0,
        0,
        null
      )
    }

    val resultsLength = results.length
    val averageOfWiningPrices = results.map(w => NumberHelper.getAsDouble(w.actualwiningprice)).sum / resultsLength
    val lastWiningPrice = NumberHelper.getAsDouble(results.head.actualwiningprice)

    WinningPricesModel(
      averageOfWiningPrices,
      lastWiningPrice,
      results.head
    )
  }
}
