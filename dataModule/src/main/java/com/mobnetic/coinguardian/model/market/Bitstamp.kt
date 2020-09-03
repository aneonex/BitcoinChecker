package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import org.json.JSONObject

class Bitstamp : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Bitstamp"
        private const val TTS_NAME = NAME
        private const val URL = "https://www.bitstamp.net/api/v2/ticker/%1\$s%2\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[Currency.EUR] = arrayOf(
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[Currency.GBP] = arrayOf(
                    Currency.EUR,
                    Currency.USD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[VirtualCurrency.XLM] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
            CURRENCY_PAIRS[VirtualCurrency.XRP] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.USD,
                    Currency.GBP
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBaseLowerCase, checkerInfo.currencyCounterLowerCase)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last")
        ticker.timestamp = jsonObject.getLong("timestamp")
    }
}