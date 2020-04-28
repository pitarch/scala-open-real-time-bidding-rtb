package shared.com.ortb.demadSidePlatforms

import shared.com.ortb.constants.AppConstants
import shared.com.ortb.demadSidePlatforms.traits.logics.{ DemandSidePlatformBiddingLogic, DemandSidePlatformStaticBidResponseLogic }
import shared.com.ortb.demadSidePlatforms.traits.properties.{ DemandSidePlatformBiddingProperties, DemandSidePlatformCorePropertiesContracts }
import shared.com.ortb.enumeration.DemandSidePlatformBiddingAlgorithmType.DemandSidePlatformBiddingAlgorithmType
import shared.com.ortb.importedModels.biddingRequests._
import shared.com.ortb.manager.AppManager
import shared.com.ortb.manager.traits.DefaultExecutionContextManager
import shared.com.ortb.model._
import shared.com.ortb.model.config.DemandSidePlatformConfigurationModel
import shared.com.ortb.model.results.DspBidderRequestModel
import shared.com.ortb.persistent
import shared.com.ortb.persistent.repositories.AdvertiseRepository
import shared.com.ortb.persistent.schema.Tables
import shared.com.ortb.persistent.schema.Tables._
import shared.com.repository.traits.FutureToRegular
import shared.io.helpers.EmptyValidateHelper
import shared.io.loggers.AppLogger
import slick.dbio.Effect
import slick.jdbc.SQLiteProfile.api._
import slick.sql.FixedSqlStreamingAction

import scala.concurrent.Future

class DemandSidePlatformBiddingAgent(
  coreProperties : DemandSidePlatformCorePropertiesContracts,
  algorithmType : DemandSidePlatformBiddingAlgorithmType)
  extends DemandSidePlatformBiddingLogic
    with DemandSidePlatformBiddingProperties
    with DefaultExecutionContextManager {

  lazy val defaultLimit : Int = coreProperties.demandSidePlatformConfiguration.defaultGenericLimit
  lazy val defaultAdvertiseLimit : Int = coreProperties.demandSidePlatformConfiguration.defaultAdvertiseLimit
  lazy val demandSidePlatformStaticBidResponseLogic : DemandSidePlatformStaticBidResponseLogic = new
      DemandSidePlatformStaticBidResponseLogicImplementation(coreProperties)

  override def getBidPrices(
    request : DspBidderRequestModel) : Option[DemandSidePlatformBidResponseModel] = ???

  def getLastFailedDeals(
    request : DspBidderRequestModel,
    limit : Int = defaultLimit) : BidFailedReasonsModel = {
    val demandSidePlatformId = coreProperties.demandSideId
    val repositories = coreProperties.repositories
    val lostBids = repositories.lostBids

    val lostBidsSqlProfileAction = lostBids
      .filter(r => r.demandsideplatformid === demandSidePlatformId)
      .sortBy(_.losingprice.desc)
      .sortBy(_.createddate.desc)
      .take(limit)
      .result

    val results = repositories.lostBidRepository
      .run(lostBidsSqlProfileAction)

    BidFailedReasonsModel(
      results
    )
  }

  def appendQueryForBanner(
    advertisesQueryIn : Query[Tables.Advertise, Tables.AdvertiseRow, Seq],
    banner : BannerModel
  ) : Query[Tables.Advertise, Tables.AdvertiseRow, Seq] = {
    var advertisesQuery = advertisesQueryIn
    if (EmptyValidateHelper.isDefined(banner.wmax)) {
      val maxWidth = banner.wmax.get.toDouble
      advertisesQuery = advertisesQuery.filter(ad => ad.maxwidth <= maxWidth)
    }

    if (EmptyValidateHelper.isDefined(banner.wmin)) {
      val minWidth = banner.wmin.get.toDouble
      advertisesQuery = advertisesQuery.filter(ad => ad.minwidth >= minWidth)
    }

    if (EmptyValidateHelper.isDefined(banner.hmax)) {
      val maxHeight = banner.hmax.get.toDouble
      advertisesQuery = advertisesQuery.filter(ad => ad.maxheight <= maxHeight)
    }

    if (EmptyValidateHelper.isDefined(banner.hmin)) {
      val minHeight = banner.hmin.get.toDouble
      advertisesQuery = advertisesQuery.filter(ad => ad.minheight >= minHeight)
    }

    if (EmptyValidateHelper.isDefined(banner.hmax)) {
      val maxHeight = banner.hmax.get.toDouble
      advertisesQuery = advertisesQuery.filter(ad => ad.maxheight <= maxHeight)
    }

    advertisesQuery
  }

  def getImpressionBiddableInfoModel(
    advertisesTable : TableQuery[Tables.Advertise],
    advertiseRepository : AdvertiseRepository,
    impression : ImpressionModel,
    limit : Int = defaultAdvertiseLimit) : Option[ImpressionBiddableInfoModel] = {
    val methodName = "getImpressionBiddableInfoModel"

    if (EmptyValidateHelper.isEmpty(impression.banner)) {
      val model = ImpressionBiddableInfoModel(
        impression,
        None,
        ImpressionBiddableAttributesModel(
          isBiddable = false,
          hasBanner = false,
          0))

      return Some(model)
    }

    val banner = impression.banner.get
    val advertisesQueryIn : Query[Tables.Advertise, Tables.AdvertiseRow, Seq] = advertisesTable.filter(advertise =>
      advertise.isvideo === 0)

    val advertisesQuery = appendQueryForBanner(advertisesQueryIn, banner)
    val countQuery = advertisesQuery.length.result
    val query = advertisesQuery.take(limit).result

    val totalCount = advertiseRepository.count(countQuery)
    val rows = advertiseRepository.run(query)
    val isBiddable = rows.nonEmpty

    val impressionAttributes = ImpressionBiddableAttributesModel(
      isBiddable,
      hasBanner = true,
      totalCount.get)

    val model = ImpressionBiddableInfoModel(
      impression,
      Some(rows.toArray),
      impressionAttributes)

    val logModel = LogTraceModel(
      methodName,
      request = Some(impression),
      entity = Some(model))

    coreProperties.databaseLogger.trace(logModel)

    Some(model)
  }

  def getBiddableImpressionInfoModels(
    request : DspBidderRequestModel,
    limit : Int = defaultAdvertiseLimit) : Seq[ImpressionBiddableInfoModel] = {
    val repositories = coreProperties.repositories
    val advertiseRepository = repositories.advertiseRepository
    val advertises : TableQuery[Advertise] = repositories.advertises
    val impressions = request.bidRequest.imp.get

    val futureTasks : Seq[Future[ImpressionBiddableInfoModel]] =
      impressions.map(impression => {
        Future(
          getImpressionBiddableInfoModel(
            advertises,
            advertiseRepository,
            impression,
            limit).get
        )
      })

    futureTasks.map(futureTask => FutureToRegular.toRegular(futureTask))
  }

  def addNewAdvertiseIfNoAdvertiseInTheGivenCriteria(
    request : DspBidderRequestModel,
    isEmpty : Boolean,
    impressionBiddableInfoModel : ImpressionBiddableInfoModel) : Unit = {
    if (!demandSidePlatformConfiguration.isAddNewAdvertiseOnNotFound || !isEmpty) {
      return
    }

    // create DSP -> campaign ->
    val methodName = "addNewAdvertiseIfNoAdvertiseInTheGivenCriteria"
    AppLogger.debug(methodName, "Adding Advertise as per configuration")

    val demandSideId = coreProperties.demandSideId

    // get publisher
    val repositories = coreProperties.repositories
    val publishersRepository = repositories.publisherRepository
    val campaignRepository = repositories.campaignRepository
    val advertiseRepository = repositories.advertiseRepository
    val keywordRepository = repositories.keywordRepository
    val keywordAdvertiseMappingRepository = repositories.keywordAdvertiseMappingRepository

    val publisherByDspQuery : FixedSqlStreamingAction[Seq[persistent.schema.Tables.PublisherRow], persistent.schema.Tables.PublisherRow, Effect.Read] = publishersRepository
      .getAllQuery
      .filter(w => w.demandsideplatformid === coreProperties.demandSideId)
      .take(1)
      .result

    val impressionModel = impressionBiddableInfoModel.impression
    val bidReq = request.bidRequest
    val site = bidReq.site

    def getCategories(site : Option[SiteModel]) : Option[List[String]] = {
      if (site.isEmpty) {
        return None
      }

      if (site.get.cat.isDefined) {
        return site.get.cat
      }

      None
    }

    def getCategoryId(site : Option[SiteModel]) : Option[String] = {
      val categories = getCategories(site)
      if (EmptyValidateHelper.hasAnyItem(categories)) {
        return Some(categories.get.head)
      }

      None
    }

    def getAsDouble(d : Option[Double]) : Double = {
      if (d.isEmpty) {
        return 0
      }

      d.get
    }

    val publisher = publishersRepository
      .getFirstOrDefaultFromQuery(publisherByDspQuery)

    // create campaign
    val bannerString = impressionModel.banner.get.toString
    val budget : Double = 1500
    val publisherId = publisher.get.publisherid
    val campaignRow = CampaignRow(
      -1,
      s"Generated: Campaign - Banner($bannerString)",
      getCategoryId(site),
      budget,
      0,
      budget,
      AppConstants.EmptyDoubleOption,
      AppConstants.EmptyDoubleOption,
      0,
      demandsideplatformid = demandSideId,
      1,
      isretricttousergender = 0,
      expectedusergender = None,
      publisherid = Some(publisherId))

    val campaignCreated = campaignRepository.add(campaignRow)
    val campaignId = campaignCreated.getIdAsInt
    val contextTextId = 5

//    val advertise = AdvertiseRow(
//      -1,
//      campaignId.get,
//      1,
//      s"Generated Banner Advertise($bannerString)",
//      Some(contextTextId),
//      s"BidUrl for $bannerString",
//      Some(s"IFrame for $bannerString"),
//      0,
//      isvideo = 0,
//      impressioncount = 0)
    throw new NotImplementedError()
  }

  def getImpressionDealsFromBiddableImpressionInfoModels(
    request : DspBidderRequestModel,
    biddableImpressionInfoModels : Seq[ImpressionBiddableInfoModel]) :
  Option[List[ImpressionDealModel]] = {
    if (EmptyValidateHelper.isItemsEmpty(Some(biddableImpressionInfoModels))) {
      return None
    }

    val list = biddableImpressionInfoModels.map(b => {
      val attr = b.attributes
      val isEmpty = !attr.isBiddable && attr.advertisesFoundCount == 0
      addNewAdvertiseIfNoAdvertiseInTheGivenCriteria(
        request,
        isEmpty,
        b)
    }).toList

    Some(list)
    throw new NotImplementedError()
  }

  override def getBidActual(
    request : DspBidderRequestModel) : Option[DemandSidePlatformBidResponseModel] = {
    val biddableImpressionInfoModels = getBiddableImpressionInfoModels(
      request : DspBidderRequestModel)

    getImpressionDealsFromBiddableImpressionInfoModels(request, biddableImpressionInfoModels)

    throw new NotImplementedError()
  }

  override def getBidActualNoContent(
    request : DspBidderRequestModel) : Option[DemandSidePlatformBidResponseModel] = {
    val dspBidderResultModel =
      DemandSidePlatformBidResponseModel(request, request.bidRequest, isNoContent = true)

    val callStackModel = CallStackModel(
      deal = coreProperties.noDealPrice,
      performingAction = s"[getBidActualNoContent] -> No deals."
    )

    dspBidderResultModel.addCallStack(callStackModel)

    Some(dspBidderResultModel)
  }

  override def getBidStatic(request : DspBidderRequestModel) : Option[DemandSidePlatformBidResponseModel] =
    demandSidePlatformStaticBidResponseLogic.getBidStatic(request)

  override def isStatic : Boolean = demandSidePlatformConfiguration.isStaticSimulate

  lazy override val demandSidePlatformConfiguration : DemandSidePlatformConfigurationModel =
    coreProperties.demandSidePlatformConfiguration

  override def getBidStaticNoContent(request : DspBidderRequestModel) : Option[DemandSidePlatformBidResponseModel] =
    demandSidePlatformStaticBidResponseLogic.getBidStaticNoContent(request)

  lazy override val appManager : AppManager = coreProperties.appManager
}
