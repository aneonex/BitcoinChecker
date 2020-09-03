package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import org.json.JSONObject
import java.util.*

class LakeBTC : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val pairId: String?
        pairId = if (checkerInfo.currencyPairId == null) {
            checkerInfo.currencyBaseLowerCase + checkerInfo.currencyCounterLowerCase
        } else {
            checkerInfo.currencyPairId
        }
        val pairJsonObject = jsonObject.getJSONObject(pairId)
        ticker.bid = pairJsonObject.getDouble("bid")
        ticker.ask = pairJsonObject.getDouble("ask")
        ticker.vol = pairJsonObject.getDouble("volume")
        ticker.high = pairJsonObject.getDouble("high")
        ticker.low = pairJsonObject.getDouble("low")
        ticker.last = pairJsonObject.getDouble("last")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val pairsJsonArray = jsonObject.names()
        for (i in 0 until pairsJsonArray.length()) {
            val pairId = pairsJsonArray.getString(i)
            val currencyBase = pairId.substring(0, 3).toUpperCase(Locale.ENGLISH)
            val currencyCounter = pairId.substring(3).toUpperCase(Locale.ENGLISH)
            pairs.add(CurrencyPairInfo(currencyBase, currencyCounter, pairId))
        }
    }

    companion object {
        private const val NAME = "LakeBTC"
        private const val TTS_NAME = "Lake BTC"
        private const val URL = "https://api.lakebtc.com/api_v2/ticker"
        private const val URL_CURRENCY_PAIRS = URL
    }
}