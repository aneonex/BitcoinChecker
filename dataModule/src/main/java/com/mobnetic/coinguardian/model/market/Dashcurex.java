package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Dashcurex : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Dashcurex"
        private const val TTS_NAME = NAME
        private const val URL = "https://dashcurex.com/api/%1\$s/ticker.json"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.DASH] = arrayOf(
                    Currency.PLN,
                    Currency.USD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyCounterLowerCase)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = parsePrice(jsonObject.getDouble("best_ask"))
        ticker.ask = parsePrice(jsonObject.getDouble("best_bid"))
        ticker.vol = parseBTC(jsonObject.getDouble("total_volume"))
        ticker.high = parsePrice(jsonObject.getDouble("highest_tx_price"))
        ticker.low = parsePrice(jsonObject.getDouble("lowest_tx_price"))
        ticker.last = parsePrice(jsonObject.getDouble("last_tx_price"))
    }

    private fun parsePrice(price: Double): Double {
        return price / 10000
    }

    private fun parseBTC(satoshi: Double): Double {
        return satoshi / 100000000
    }
}