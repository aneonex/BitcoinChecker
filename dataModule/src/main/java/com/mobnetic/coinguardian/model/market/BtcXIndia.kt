package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.util.ParseUtils
import org.json.JSONObject

class BtcXIndia : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "BTCXIndia"
        private const val TTS_NAME = "BTC X India"
        private const val URL = "https://api.btcxindia.com/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.XRP] = arrayOf(
                    Currency.INR
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("total_volume_24h")
        ticker.high = ParseUtils.getDoubleFromString(jsonObject, "high")
        ticker.low = ParseUtils.getDoubleFromString(jsonObject, "low")
        ticker.last = jsonObject.getDouble("last_traded_price")
    }
}