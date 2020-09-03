package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class ItBit : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "itBit"
        private const val TTS_NAME = "It Bit"
        private const val URL = "https://api.itbit.com/v1/markets/%1\$s%2\$s/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD,
                    Currency.SGD,
                    Currency.EUR
            )
        }
    }

    private fun fixCurrency(currency: String?): String? {
        return if (VirtualCurrency.BTC == currency) VirtualCurrency.XBT else currency
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, fixCurrency(checkerInfo.currencyBase), checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume24h")
        ticker.high = jsonObject.getDouble("high24h")
        ticker.low = jsonObject.getDouble("low24h")
        ticker.last = jsonObject.getDouble("lastPrice")
    }

    @Throws(Exception::class)
    override fun parseErrorFromJsonObject(requestId: Int, jsonObject: JSONObject, checkerInfo: CheckerInfo?): String? {
        return jsonObject.getString("message") // not working?
    }
}