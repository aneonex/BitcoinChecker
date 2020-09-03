package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class SatoshiTango : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "SatoshiTango"
        private const val TTS_NAME = "Satoshi Tango"
        private const val URL = "https://api.satoshitango.com/v2/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD,
                    Currency.ARS,
                    Currency.EUR
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val currencyCode = checkerInfo.currencyCounterLowerCase + checkerInfo.currencyBaseLowerCase
        val tickerJsonObject = jsonObject.getJSONObject("data")
        val buyObject = tickerJsonObject.getJSONObject("compra")
        val sellObject = tickerJsonObject.getJSONObject("venta")
        ticker.ask = buyObject.getDouble(currencyCode)
        ticker.bid = sellObject.getDouble(currencyCode)
        ticker.last = ticker.ask
    }
}