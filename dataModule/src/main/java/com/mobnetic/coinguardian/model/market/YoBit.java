package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardiandatamodule.R
import org.json.JSONObject
import java.util.*

class YoBit : Market(NAME, TTS_NAME, null) {
    override val cautionResId: Int
        get() = R.string.market_caution_yobit

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyPairId)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val names = jsonObject.names()
        val tickerJsonObject = jsonObject.getJSONObject(names.getString(0))
        ticker.bid = tickerJsonObject.getDouble("sell")
        ticker.ask = tickerJsonObject.getDouble("buy")
        ticker.vol = tickerJsonObject.getDouble("vol")
        ticker.high = tickerJsonObject.getDouble("high")
        ticker.low = tickerJsonObject.getDouble("low")
        ticker.last = tickerJsonObject.getDouble("last")
        ticker.timestamp = tickerJsonObject.getLong("updated")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val pairsJsonObject = jsonObject.getJSONObject("pairs")
        val pairsNames = pairsJsonObject.names()
        for (i in 0 until pairsNames.length()) {
            val pairId = pairsNames.getString(i) ?: continue
            val currencies = pairId.split("_".toRegex()).toTypedArray()
            if (currencies.size != 2) continue
            val currencyBase = currencies[0].toUpperCase(Locale.ENGLISH)
            val currencyCounter = currencies[1].toUpperCase(Locale.ENGLISH)
            pairs.add(CurrencyPairInfo(currencyBase, currencyCounter, pairId))
        }
    }

    companion object {
        private const val NAME = "YoBit"
        private const val TTS_NAME = NAME
        private const val URL = "https://yobit.net/api/3/ticker/%1\$s"
        private const val URL_CURRENCY_PAIRS = "https://yobit.net/api/3/info"
    }
}