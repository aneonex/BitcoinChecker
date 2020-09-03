package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Coinbase : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Coinbase"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.gdax.com/products/%1\$s-%2\$s/ticker"
        private const val URL_CURRENCY_PAIRS = "https://api.gdax.com/products/"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD,
                    Currency.EUR,
                    Currency.GBP
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    Currency.USD,
                    Currency.EUR,
                    VirtualCurrency.BTC
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    Currency.USD,
                    Currency.EUR,
                    VirtualCurrency.BTC
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.last = jsonObject.getDouble("price")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairs(requestId: Int, responseString: String?, pairs: MutableList<CurrencyPairInfo?>) {
        val jsonArray = JSONArray(responseString)
        for (i in 0 until jsonArray.length()) {
            val pairJsonObject = jsonArray.getJSONObject(i)
            pairs.add(CurrencyPairInfo(
                    pairJsonObject.getString("base_currency"),
                    pairJsonObject.getString("quote_currency"),
                    pairJsonObject.getString("id")))
        }
    }
}