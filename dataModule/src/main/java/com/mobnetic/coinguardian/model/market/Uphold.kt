// @joseccnet contribution.
package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.util.ParseUtils
import org.json.JSONArray
import org.json.JSONObject

class Uphold : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyPairId)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = ParseUtils.getDoubleFromString(jsonObject, "bid")
        ticker.ask = ParseUtils.getDoubleFromString(jsonObject, "ask")
        ticker.last = (ticker.bid + ticker.ask) / 2 //This is how Uphold operate on production (as I observed)
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairs(requestId: Int, responseString: String?, pairs: MutableList<CurrencyPairInfo?>) {
        val pairsJsonArray = JSONArray(responseString)
        for (i in 0 until pairsJsonArray.length()) {
            val pairJsonObject = pairsJsonArray.getJSONObject(i)
            val pairId = pairJsonObject.getString("pair")
            val currencyCounter = pairJsonObject.getString("currency")
            if (currencyCounter != null && pairId != null && pairId.endsWith(currencyCounter)) {
                val currencyBase = pairId.substring(0, pairId.length - currencyCounter.length)
                if (currencyCounter != currencyBase) {
                    // normal pair
                    pairs.add(CurrencyPairInfo(
                            currencyBase,
                            currencyCounter,
                            pairId))
                    // reversed pair
                    pairs.add(CurrencyPairInfo(
                            currencyCounter,
                            currencyBase,
                            currencyCounter + currencyBase))
                }
            }
        }
    }

    companion object {
        private const val NAME = "Uphold"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.uphold.com/v0/ticker/%1\$s"
        private const val URL_CURRENCY_PAIRS = "https://api.uphold.com/v0/ticker"
    }
}