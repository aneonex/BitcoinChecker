package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import org.json.JSONObject

class Koinex : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val names = jsonObject.getJSONObject("stats")
        val tickerJsonObject = names.getJSONObject(checkerInfo.currencyBase)
        ticker.bid = tickerJsonObject.getDouble("highest_bid")
        ticker.ask = tickerJsonObject.getDouble("lowest_ask")
        ticker.vol = tickerJsonObject.getDouble("vol_24hrs")
        ticker.high = tickerJsonObject.getDouble("max_24hrs")
        ticker.low = tickerJsonObject.getDouble("min_24hrs")
        ticker.last = tickerJsonObject.getDouble("last_traded_price")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val currencyJSONObject = jsonObject.getJSONObject("stats")
        val currencyArray = currencyJSONObject.names()
        for (i in 0 until currencyArray.length()) {
            pairs.add(CurrencyPairInfo(currencyArray.getString(i), Currency.INR, null))
        }
    }

    companion object {
        private const val NAME = "Koinex"
        private const val TTS_NAME = "Koin ex"
        private const val URL = "https://koinex.in/api/ticker"
        private const val URL_CURRENCY_PAIRS = URL
    }
}