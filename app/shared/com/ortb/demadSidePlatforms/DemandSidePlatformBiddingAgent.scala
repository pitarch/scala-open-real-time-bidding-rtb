package shared.com.ortb.demadSidePlatforms

import shared.com.ortb.demadSidePlatforms.traits.getters._
import shared.com.ortb.demadSidePlatforms.traits.logics._
import shared.com.ortb.demadSidePlatforms.traits.properties.{ DemandSidePlatformBiddingProperties, DemandSidePlatformCorePropertiesContracts }
import shared.com.ortb.demadSidePlatforms.traits.{ AddNewAdvertiseOnNotFound, DefaultActualNoContentResponse }
import shared.com.ortb.enumeration.DemandSidePlatformBiddingAlgorithmType.DemandSidePlatformBiddingAlgorithmType
import shared.com.ortb.manager.traits.CreateDefaultContext
import shared.com.ortb.model.auctionbid.bidresponses.BidResponseModel
import shared.com.ortb.model.auctionbid.{ DemandSidePlatformBidResponseModel, ImpressionDealModel }
import shared.com.ortb.model.config.DemandSidePlatformConfigurationModel
import shared.com.ortb.model.results.DemandSidePlatformBiddingRequestWrapperModel
import shared.com.ortb.persistent.schema.Tables._
import shared.io.helpers.{ EmptyValidateHelper, JodaDateTimeHelper }

import scala.concurrent.ExecutionContext

class DemandSidePlatformBiddingAgent(
  val coreProperties : DemandSidePlatformCorePropertiesContracts,
  algorithmType : DemandSidePlatformBiddingAlgorithmType)
  extends DemandSidePlatformBiddingLogic
    with DemandSidePlatformBiddingProperties
    with AddNewAdvertiseOnNotFound
    with CreateDefaultContext
    with BiddableInfoModelsGetter
    with AdvertiseQueryAppendLogic
    with FailedBidsGetter
    with ImpressionDealsGetter
    with ExactHeightWidthQueryRowsGetter
    with DefaultActualNoContentResponse {
  lazy val defaultLimit : Int = coreProperties.demandSidePlatformConfiguration.defaultGenericLimit
  lazy val defaultAdvertiseLimit : Int = coreProperties.demandSidePlatformConfiguration.defaultAdvertiseLimit
  lazy val demandSidePlatformStaticBidResponseLogic : DemandSidePlatformStaticBidResponseLogic = new
      DemandSidePlatformStaticBidResponseLogicImplementation(coreProperties)

  implicit override def createDefaultContext() : ExecutionContext =
    executionContextManager.defaultExecutionContext

  override def getBidStatic(request : DemandSidePlatformBiddingRequestWrapperModel) : Option[DemandSidePlatformBidResponseModel] =
    demandSidePlatformStaticBidResponseLogic.getBidStatic(request)

  override def isStatic : Boolean = demandSidePlatformConfiguration.isStaticSimulate

  lazy override val demandSidePlatformConfiguration : DemandSidePlatformConfigurationModel =
    coreProperties.demandSidePlatformConfiguration

  override def getBidStaticNoContent(request : DemandSidePlatformBiddingRequestWrapperModel) :
  Option[DemandSidePlatformBidResponseModel] =
    demandSidePlatformStaticBidResponseLogic.getBidStaticNoContent(request)

  override def getBidPrices(
    request : DemandSidePlatformBiddingRequestWrapperModel) : Option[DemandSidePlatformBidResponseModel] = ???

  def createDemandSidePlatformBidResponse(
    request : DemandSidePlatformBiddingRequestWrapperModel,
    impressionDeals : Option[List[ImpressionDealModel]]) :
  Option[DemandSidePlatformBidResponseModel] = {
    // create bid response
    val bidResponse = BidresponseRow(
      -1,
      bidrequestid = Some(request.bidRequestRow.bidrequestid),
      isauctionoccured = 1,
      createddatetimestamp = JodaDateTimeHelper.nowUtcJavaInstant
    )

    val bidResponseRepository = coreProperties
      .repositories
      .bidResponseRepository

    val bidResponseId = bidResponseRepository
      .add(bidResponse)
      .getIdAsInt
      .get

    // create bid

    ???
  }

  override def getBidActual(
    request : DemandSidePlatformBiddingRequestWrapperModel) : Option[DemandSidePlatformBidResponseModel] = {
    val biddableImpressionInfoModels = getBiddableImpressionInfoModels(
      request : DemandSidePlatformBiddingRequestWrapperModel)

    val impressionDeals =
      getImpressionInfoModelsFromImpressionBiddableInfoModels(
        request,
        biddableImpressionInfoModels)

    if (EmptyValidateHelper.isItemsEmpty(impressionDeals) || !impressionDeals.get.exists(w => w.hasAnyDeal)) {
      return getBidActualNoContent(request)
    }

    createDemandSidePlatformBidResponse(request, impressionDeals)
  }

  def getBidResponseRow(bidResponse : Option[BidResponseModel]) : BidresponseRow = ???

  def createNoBidResponseToDbAsync() : Unit = {
    val row = BidresponseRow(
      -1,
      createddatetimestamp = JodaDateTimeHelper.nowUtcJavaInstant)

    coreProperties
      .repositories
      .bidResponseRepository
      .addAsync(row)
  }

  def addBidResponseAsync(response : DemandSidePlatformBidResponseModel) : Unit = {
    if (EmptyValidateHelper.isEmptyDirect(
      response,
      Some("addBidResponse, given response is empty. Nothing to save."))) {
      return
    }

    val bidResponse = response.bidResponseWrapper.bidResponse
    if (EmptyValidateHelper.isEmpty(
      bidResponse,
      Some("addBidResponse, given bidResponse is empty. Nothing to save."))) {
      // create no bid response
      createNoBidResponseToDbAsync()
      return
    }

    val bidResponseRepository = coreProperties
      .repositories
      .bidResponseRepository

    val bidResponseRow = getBidResponseRow(bidResponse)

    bidResponseRepository.addAsync(bidResponseRow)
  }
}
