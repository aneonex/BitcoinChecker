package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class Korbit : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Korbit"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.korbit.co.kr/v1/ticker/detailed?currency_pair=%1\$s_%2\$s"
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
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBaseLowerCase,
                checkerInfo.currencyCounterLowerCase)
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