package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Gemini : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Gemini"
        private const val TTS_NAME = "Gemini"
        private const val URL = "https://api.gemini.com/v1/book/%1\$s%2\$s?limit_asks=1&limit_bids=1"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        // Gemini allows dynamic symbol retrieval but returns it in format: [ "btcusd" ]
        // This doesn't provide an easy way to programmatically split the currency
        // So just define them
        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.USD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        //Gemini isn't a traditional tracker, rather it seems to return the X last bids and asks
        //We could do something like take the average of the last Y prices
        //But I will just take the average of the last bid and asking price
        val bidsArray = jsonObject.getJSONArray("bids")
        if (bidsArray != null && bidsArray.length() > 0) {
            ticker.bid = bidsArray.getJSONObject(0).getDouble("price")
        }
        val asksArray = jsonObject.getJSONArray("asks")
        if (asksArray != null && asksArray.length() > 0) {
            ticker.ask = asksArray.getJSONObject(0).getDouble("price")
        }
        if (ticker.bid != Ticker.Companion.NO_DATA.toDouble() && ticker.ask != Ticker.Companion.NO_DATA.toDouble()) {
            ticker.last = (ticker.bid + ticker.ask) / 2.0
        } else if (ticker.bid != Ticker.Companion.NO_DATA.toDouble()) {
            ticker.last = ticker.bid
        } else if (ticker.ask != Ticker.Companion.NO_DATA.toDouble()) {
            ticker.last = ticker.ask
        } else {
            ticker.last = 0.0
        }
    }
}