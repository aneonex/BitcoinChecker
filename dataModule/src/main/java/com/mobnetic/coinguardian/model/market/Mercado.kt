package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.util.TimeUtils
import org.json.JSONObject
import java.util.*

class Mercado : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Mercado Bitcoin"
        private const val TTS_NAME = "Mercado"
        private const val URL = "https://www.mercadobitcoin.com.br/api/%1\$s/ticker/"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.BRL
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    Currency.BRL
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    Currency.BRL
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val tickerJsonObject = jsonObject.getJSONObject("ticker")
        ticker.bid = tickerJsonObject.getDouble("buy")
        ticker.ask = tickerJsonObject.getDouble("sell")
        ticker.vol = tickerJsonObject.getDouble("vol")
        ticker.high = tickerJsonObject.getDouble("high")
        ticker.low = tickerJsonObject.getDouble("low")
        ticker.last = tickerJsonObject.getDouble("last")
        ticker.timestamp = tickerJsonObject.getLong("date") * TimeUtils.MILLIS_IN_SECOND
    }
}