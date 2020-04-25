package shared.com.ortb.importedModels.biddingRequests

case class BidRequest(
  id : String,
  imp : Option[List[Impression]],
  site : Option[Site],
  user : Option[User],
  device : Option[Device])