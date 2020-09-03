package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class CoinSecure : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "CoinSecure"
        private const val TTS_NAME = "Coin Secure"
        private const val URL = "https://api.coinsecure.in/v1/exchange/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.INR
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val messageJsonObject = jsonObject.getJSONObject("message")
        ticker.bid = parsePrice(messageJsonObject.getDouble("bid"))
        ticker.ask = parsePrice(messageJsonObject.getDouble("ask"))
        ticker.vol = messageJsonObject.getDouble("coinvolume") / 100000000
        ticker.high = parsePrice(messageJsonObject.getDouble("high"))
        ticker.low = parsePrice(messageJsonObject.getDouble("low"))
        ticker.last = parsePrice(messageJsonObject.getDouble("lastPrice"))
        //		ticker.timestamp = messageJsonObject.getLong("timestamp");
    }

    private fun parsePrice(price: Double): Double {
        return price / 100
    }
}