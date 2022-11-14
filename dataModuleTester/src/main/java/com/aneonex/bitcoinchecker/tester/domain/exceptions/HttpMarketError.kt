package com.aneonex.bitcoinchecker.tester.domain.exceptions

class HttpMarketError(val httpCode: Int, val responseString: String?) : MarketError() {
    override val message: String
        get() = "HttpCode: $httpCode"
}


