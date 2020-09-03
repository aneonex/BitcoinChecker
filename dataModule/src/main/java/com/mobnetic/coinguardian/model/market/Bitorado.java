package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import org.json.JSONObject

class Bitorado : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val resultObject = jsonObject.getJSONObject("result")
        ticker.bid = resultObject.optDouble("buy", Ticker.Companion.NO_DATA.toDouble())
        ticker.ask = resultObject.optDouble("sell", Ticker.Companion.NO_DATA.toDouble())
        ticker.vol = resultObject.optDouble("vol", Ticker.Companion.NO_DATA.toDouble())
        ticker.high = resultObject.optDouble("high", Ticker.Companion.NO_DATA.toDouble())
        ticker.low = resultObject.optDouble("low", Ticker.Companion.NO_DATA.toDouble())
        ticker.last = resultObject.optDouble("last", 0.0)
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val result = jsonObject.getJSONObject("result")
        val markets = result.getJSONObject("markets")
        val pairNames = markets.names()
        for (i in 0 until pairNames.length()) {
            val pairId = pairNames.getString(i) ?: continue
            val currencies = pairId.split("-".toRegex()).toTypedArray()
            if (currencies.size != 2) continue
            pairs.add(CurrencyPairInfo(currencies[0], currencies[1], pairId))
        }
    }

    companion object {
        private const val NAME = "Bitorado"
        private const val TTS_NAME = NAME
        private const val URL = "https://www.bitorado.com/api/market/%1\$s-%2\$s/ticker"
        private const val URL_CURRENCY_PAIRS = "https://www.bitorado.com/api/ticker"
    }
}