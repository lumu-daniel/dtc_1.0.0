package com.mlt.dtc.networking;

public
class ServicesUrls {

    //Weatehr service
    public static final String UserLoginURL = "http://dtcwbsvc.networkips.com/ServiceModule/DTCService.svc/";

    //Common service
    public static final String URL = "https://dtcpushservice.networkips.com:6104/ServiceModule/DTCPushService.svc/"; //UAT

//    public static final String URL = "https://dtc.networkips.com:6105/ServiceModule/DTCPushService.svc/";//Prod

    //Production URL for Taxi meter subscription service
    public static final String URL_TaxiMS = "https://dtc.networkips.com/NIPSDTCService/NIPSDTCServices.svc/";

    //SOAP Action
    public static final String URLSOAPAction = "http://tempuri.org/IDTCPushService/Inquiry";

}
