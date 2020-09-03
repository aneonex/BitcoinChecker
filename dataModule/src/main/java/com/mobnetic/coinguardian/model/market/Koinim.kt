package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Koinim : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        const val NAME = "Koinim"
        const val TTS_NAME = NAME
        const val URL_BTC = "https://koinim.com/ticker/"
        const val URL_LTC = "https://koinim.com/ticker/ltc/"
        val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.TRY
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    Currency.TRY
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return if (VirtualCurrency.LTC == checkerInfo.currencyBase) {
            URL_LTC
        } else URL_BTC
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("buy")
        ticker.ask = jsonObject.getDouble("sell")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last_order")
    }
}