package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class CoinMateIO : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "CoinMate.io"
        private const val TTS_NAME = "Coin Mate"
        private const val URL = "https://coinmate.io/api/ticker?currencyPair=%1\$s_%2\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.EUR,
                    Currency.CZK
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val dataJsonObject = jsonObject.getJSONObject("data")
        ticker.bid = dataJsonObject.getDouble("bid")
        ticker.ask = dataJsonObject.getDouble("ask")
        ticker.vol = dataJsonObject.getDouble("amount")
        ticker.high = dataJsonObject.getDouble("high")
        ticker.low = dataJsonObject.getDouble("low")
        ticker.last = dataJsonObject.getDouble("last")
    }

    @Throws(Exception::class)
    override fun parseErrorFromJsonObject(requestId: Int, jsonObject: JSONObject, checkerInfo: CheckerInfo?): String? {
        return jsonObject.getString("errorMessage")
    }
}