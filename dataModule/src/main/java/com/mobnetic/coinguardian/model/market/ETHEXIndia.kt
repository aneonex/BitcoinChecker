package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class ETHEXIndia : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "ETHEX India"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.ethexindia.com/ticker/"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    Currency.INR
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
        ticker.vol = jsonObject.getDouble("current_volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last_traded_price")
        ticker.timestamp = jsonObject.getLong("last_traded_time")
    }
}