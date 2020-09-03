package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import org.json.JSONArray
import org.json.JSONObject

class CoinSwap : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("dayvolume")
        ticker.high = jsonObject.getDouble("dayhigh")
        ticker.low = jsonObject.getDouble("daylow")
        ticker.last = jsonObject.getDouble("lastprice")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairs(requestId: Int, responseString: String?, pairs: MutableList<CurrencyPairInfo?>) {
        val marketsJsonArray = JSONArray(responseString)
        for (i in 0 until marketsJsonArray.length()) {
            val marketJsonObject = marketsJsonArray.getJSONObject(i)
            pairs.add(CurrencyPairInfo(
                    marketJsonObject.getString("symbol"),
                    marketJsonObject.getString("exchange"),
                    null))
        }
    }

    companion object {
        private const val NAME = "Coin-Swap"
        private const val TTS_NAME = "Coin Swap"
        private const val URL = "https://api.coin-swap.net/market/stats/%1\$s/%2\$s"
        private const val URL_CURRENCY_PAIRS = "http://api.coin-swap.net/market/summary"
    }
}