package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Unisend : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Unisend"
        private const val TTS_NAME = "Uni send"
        private const val URL = "https://www.unisend.com/api/price/ar/ars_btc"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.ARS
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val pricesJsonObject = jsonObject.getJSONObject("prices")
        ticker.bid = pricesJsonObject.getDouble("sell") // reversed!
        ticker.ask = pricesJsonObject.getDouble("buy") // reversed!
        ticker.last = ticker.ask
    }
}