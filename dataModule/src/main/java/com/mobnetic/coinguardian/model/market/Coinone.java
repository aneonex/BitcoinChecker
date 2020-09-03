package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Coinone : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Coinone"
        private const val TTS_NAME = NAME
        private const val URL_TICKER = "https://api.coinone.co.kr/ticker?currency=%1\$s"
        private const val URL_ORDERS = "https://api.coinone.co.kr/orderbook?currency=%1\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.ETC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.XRP] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.QTUM] = arrayOf(
                    Currency.KRW
            )
        }
    }

    override fun getNumOfRequests(checkerRecord: CheckerInfo?): Int {
        return 2
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return if (requestId == 0) {
            String.format(URL_TICKER, checkerInfo.currencyBaseLowerCase)
        } else {
            String.format(URL_ORDERS, checkerInfo.currencyBaseLowerCase)
        }
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker,
                                           checkerInfo: CheckerInfo) {
        if (requestId == 0) {
            ticker.vol = jsonObject.getDouble("volume")
            ticker.high = jsonObject.getDouble("high")
            ticker.low = jsonObject.getDouble("low")
            ticker.last = jsonObject.getDouble("last")
            ticker.timestamp = jsonObject.getLong("timestamp")
        } else {
            ticker.bid = getFirstPriceFromOrder(jsonObject, "bid")
            ticker.ask = getFirstPriceFromOrder(jsonObject, "ask")
        }
    }

    @Throws(Exception::class)
    private fun getFirstPriceFromOrder(jsonObject: JSONObject, key: String): Double {
        val array = jsonObject.getJSONArray(key)
        if (array.length() == 0) {
            return Ticker.Companion.NO_DATA.toDouble()
        }
        val first = array.getJSONObject(0)
        return first.getDouble("price")
    }
}