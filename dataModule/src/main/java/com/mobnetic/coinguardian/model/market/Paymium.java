package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Paymium : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Paymium"
        private const val TTS_NAME = NAME
        private const val URL = "https://paymium.com/api/v1/data/eur/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.EUR
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("price")
    }

    @Throws(Exception::class)
    override fun parseErrorFromJsonObject(requestId: Int, jsonObject: JSONObject, checkerInfo: CheckerInfo?): String? {
        val stringBuilder = StringBuilder()
        val errorsArray = jsonObject.getJSONArray("errors")
        for (i in 0 until errorsArray.length()) {
            if (stringBuilder.length != 0) {
                stringBuilder.append("\n")
            }
            stringBuilder.append(errorsArray.getString(i))
        }
        return stringBuilder.toString()
    }
}