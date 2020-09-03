package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Virtex : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "CaVirtEx"
        private const val TTS_NAME = NAME
        private const val URL = "https://cavirtex.com/api2/ticker.json"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.CAD,
                    VirtualCurrency.LTC
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    Currency.CAD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val tickerJsonObject = jsonObject.getJSONObject("ticker")
        val pairJsonObject = tickerJsonObject.getJSONObject(checkerInfo.currencyBase + checkerInfo.currencyCounter)
        if (!pairJsonObject.isNull("buy")) {
            ticker.bid = pairJsonObject.getDouble("buy")
        }
        if (!pairJsonObject.isNull("sell")) {
            ticker.ask = pairJsonObject.getDouble("sell")
        }
        if (!pairJsonObject.isNull("volume")) {
            ticker.vol = pairJsonObject.getDouble("volume")
        }
        if (!pairJsonObject.isNull("high")) {
            ticker.high = pairJsonObject.getDouble("high")
        }
        if (!pairJsonObject.isNull("low")) {
            ticker.low = pairJsonObject.getDouble("low")
        }
        ticker.last = pairJsonObject.getDouble("last")
    }
}