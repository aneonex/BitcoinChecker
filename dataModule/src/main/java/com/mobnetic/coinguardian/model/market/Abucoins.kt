package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import org.json.JSONArray
import org.json.JSONObject

class Abucoins : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyPairId)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairs(requestId: Int, responseString: String?, pairs: MutableList<CurrencyPairInfo?>) {
        val jsonArray = JSONArray(responseString)
        for (i in 0 until jsonArray.length()) {
            val pairJsonObject = jsonArray.getJSONObject(i)
            pairs.add(CurrencyPairInfo(
                    pairJsonObject.getString("base_currency"),
                    pairJsonObject.getString("quote_currency"),
                    pairJsonObject.getString("id")))
        }
    }

    companion object {
        private const val NAME = "Abucoins"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.abucoins.com/products/%1\$s/stats"
        private const val URL_CURRENCY_PAIRS = "https://api.abucoins.com/products"
    }
}