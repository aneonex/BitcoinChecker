package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import org.json.JSONArray
import org.json.JSONObject

class MintPal : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        parseTickerFromJsonObject(requestId, JSONArray(responseString).getJSONObject(0), ticker, checkerInfo)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.vol = jsonObject.getDouble("24hvol")
        ticker.high = jsonObject.getDouble("24hhigh")
        ticker.low = jsonObject.getDouble("24hlow")
        ticker.last = jsonObject.getDouble("last_price")
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
            val marketObject = jsonArray.getJSONObject(i)
            pairs.add(CurrencyPairInfo(
                    marketObject.getString("code"),
                    marketObject.getString("exchange"),
                    null
            ))
        }
    }

    companion object {
        private const val NAME = "MintPal"
        private const val TTS_NAME = "Mint Pal"
        private const val URL = "https://api.mintpal.com/market/stats/%1\$s/%2\$s/"
        private const val URL_CURRENCY_PAIRS = "https://api.mintpal.com/market/summary/"
    }
}