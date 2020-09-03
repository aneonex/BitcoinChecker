package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject

class CoinDesk : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val bpiJsonObject = jsonObject.getJSONObject("bpi")
        val pairJsonObject = bpiJsonObject.getJSONObject(checkerInfo.currencyCounter)
        ticker.last = pairJsonObject.getDouble("rate_float")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val bpiJsonObject = jsonObject.getJSONObject("bpi")
        val currencyCounterNames = bpiJsonObject.names()
        for (i in 0 until currencyCounterNames.length()) {
            pairs.add(CurrencyPairInfo(VirtualCurrency.BTC, currencyCounterNames.getString(i), null))
        }
    }

    companion object {
        private const val NAME = "CoinDesk"
        private const val TTS_NAME = "Coin Desk"
        private const val URL = "https://api.coindesk.com/v1/bpi/currentprice.json"
        private const val URL_CURRENCY_PAIRS = URL
    }
}