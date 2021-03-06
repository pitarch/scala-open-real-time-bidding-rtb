package shared.com.ortb.model.auctionbid.bidresponses

/**
 * BidResponse -> seatbid[] -> seatbid -> bid[] -> single BidModel is this one.
 *
 * @param id      : (Must) Identifier for bid
 * @param impid   : (Must) ID of the Imp (Impression) object in the related bid request.
 * @param price   : (Must) Bid price expressed as CPM although the
 *                * actual transaction is for a unit impression only.
 *                * Note that while the type indicates float,
 *                * integer math is highly recommended
 *                * when handling currencies (e.g., BigDecimal in Java).
 * @param adid    : ID of a preloaded ad to be served if the bid wins.
 * @param nurl    : Win notice URL called by the exchange if the bid wins;
 *                * optional means of serving ad markup.
 *                * The win notice URL and its format are defined by the bidder.
 *                * In order for the exchange to convey certain information to
 *                * the winning bidder (e.g., the clearing price), a number of substitution
 *                * macros can be inserted into the win notice URL definition.
 *                * Prior to calling a win notice URL, the exchange will search
 *                * the specified URL for any of the defined macros and replace
 *                * them with the appropriate data. Note that the substitution is
 *                * simple in the sense that wherever a legal macro is found,
 *                * it will be replaced without regard for syntax correctness.
 * @param adm     : Optional means of conveying ad markup in
 *                * case the bid wins; supersedes the
 *                * win notice if markup is included in both.
 * @param adomain : Advertiser domain for block list checking (e.g., “ford.com”).
 *                * This can be an array of for the case of rotating creatives.
 *                * Exchanges can mandate that only one domain is allowed.
 * @param iurl    : URL without cache-busting to an image that is
 *                * representative of the content of the
 *                * campaign for ad quality/safety checking.
 * @param cid     : Campaign ID to assist with ad quality checking; t
 *                * he collection of creatives for which iurl
 *                * should be representative.
 * @param cat     : IAB content categories
 * @param dealid  : Reference to the deal.id from the bid request
 *                * if this bid pertains to a private marketplace direct deal.
 * @param h       : Height of the creative in pixels.
 * @param w       : Width of the creative in pixels.
 */
case class BidModel(
  id : String,

  /**
   * ID of the Imp (Impression) object in the related bid request.
   */
  impid : String,

  /**
   * Bid price expressed as CPM although the
   * actual transaction is for a unit impression only.
   * Note that while the type indicates float,
   * integer math is highly recommended
   * when handling currencies (e.g., BigDecimal in Java).
   */
  price : Double,

  /**
   * ID of a preloaded ad to be served if the bid wins.
   */
  adid : Option[String] = None,

  /**
   * Win notice URL called by the exchange if the bid wins;
   * optional means of serving ad markup.
   * The win notice URL and its format are defined by the bidder.
   * In order for the exchange to convey certain information to
   * the winning bidder (e.g., the clearing price), a number of substitution
   * macros can be inserted into the win notice URL definition.
   * Prior to calling a win notice URL, the exchange will search
   * the specified URL for any of the defined macros and replace
   * them with the appropriate data. Note that the substitution is
   * simple in the sense that wherever a legal macro is found,
   * it will be replaced without regard for syntax correctness.
   * Eg. "http://adserver.com/winnotice?impid=102"
   */
  nurl : Option[String] = None,

  /**
   * Optional means of conveying ad markup in
   * case the bid wins; supersedes the
   * win notice if markup is included in both.
   *
   * Note that since there both a win notice URL and
   * an inline VAST document in the adm attribute,
   * which constitutes the ad markup. The win notice
   * is still called, but if it were to return markup
   * it would be ignored in favor of the contents of the adm attribute.
   */
  adm : Option[String] = None,

  /**
   * Advertiser domain for block list checking (e.g., “ford.com”).
   * This can be an array of for the case of rotating creatives.
   * Exchanges can mandate that only one domain is allowed.
   * Eg. [ "advertiserdomain.com" ]
   */
  adomain : Option[List[String]] = None,

  /**
   * URL without cache-busting to an image that is
   * representative of the content of the
   * campaign for ad quality/safety checking.
   * Eg. "http://adserver.com/pathtosampleimage"
   */
  iurl : Option[String] = None,


  /**
   * Campaign ID to assist with ad quality checking; t
   * he collection of creatives for which iurl
   * should be representative.
   * Eg. "campaign111"
   */
  cid : Option[String] = None,


  /**
   * IAB content categories
   * can also be like "creative112"
   */
  cat : Option[List[String]] = None,

  /**
   * Reference to the deal.id from the bid request
   * if this bid pertains to a private marketplace direct deal.
   */
  dealid : Option[String] = None,

  /**
   * Height of the creative in pixels.
   */
  h : Option[Int] = None,

  /**
   * Width of the creative in pixels.
   */
  w : Option[Int] = None
)
