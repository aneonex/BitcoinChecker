package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.util.TimeUtils
import org.json.JSONObject
import java.util.*

class Mtgox : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Mtgox"
        private const val TTS_NAME = "MT gox"
        private const val URL = "https://data.mtgox.com/api/2/%1\$s%2\$s/money/ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD,
                    Currency.EUR,
                    Currency.CAD,
                    Currency.GBP,
                    Currency.CHF,
                    Currency.RUB,
                    Currency.AUD,
                    Currency.SEK,
                    Currency.DKK,
                    Currency.HKD,
                    Currency.PLN,
                    Currency.CNY,
                    Currency.SGD,
                    Currency.THB,
                    Currency.NZD,
                    Currency.JPY
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val dataObject = jsonObject.getJSONObject("data")
        ticker.bid = getPriceValueFromObject(dataObject, "buy")
        ticker.ask = getPriceValueFromObject(dataObject, "sell")
        ticker.vol = getPriceValueFromObject(dataObject, "vol")
        ticker.high = getPriceValueFromObject(dataObject, "high")
        ticker.low = getPriceValueFromObject(dataObject, "low")
        ticker.last = getPriceValueFromObject(dataObject, "last_local")
        ticker.timestamp = dataObject.getLong("now") / TimeUtils.NANOS_IN_MILLIS
    }

    @Throws(Exception::class)
    private fun getPriceValueFromObject(jsonObject: JSONObject, key: String): Double {
        val innerObject = jsonObject.getJSONObject(key)
        return innerObject.getDouble("value")
    }
}