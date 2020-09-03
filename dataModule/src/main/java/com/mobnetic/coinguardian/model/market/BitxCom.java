package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import org.json.JSONObject

class BitxCom : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "CoinsBank"
        private const val TTS_NAME = NAME
        private const val URL = "https://coinsbank.com/api/public/ticker?pair=%1\$s%2\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.EUR,
                    Currency.GBP,
                    Currency.USD
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.GBP,
                    Currency.USD
            )
            CURRENCY_PAIRS[VirtualCurrency.GHS] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.EUR,
                    Currency.GBP,
                    VirtualCurrency.LTC,
                    Currency.USD
            )
            CURRENCY_PAIRS[Currency.EUR] = arrayOf(
                    Currency.GBP,
                    Currency.USD
            )
            CURRENCY_PAIRS[Currency.GBP] = arrayOf(
                    Currency.USD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val dataJsonObject = jsonObject.getJSONObject("data")
        ticker.bid = dataJsonObject.getDouble("buy")
        ticker.ask = dataJsonObject.getDouble("sell")
        ticker.vol = dataJsonObject.getDouble("volume")
        ticker.high = dataJsonObject.getDouble("high")
        ticker.low = dataJsonObject.getDouble("low")
        ticker.last = dataJsonObject.getDouble("last")
    }
}