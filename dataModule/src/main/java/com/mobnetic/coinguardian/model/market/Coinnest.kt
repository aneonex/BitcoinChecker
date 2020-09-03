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

class Coinnest : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Coinnest"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.coinnest.co.kr/api/pub/ticker?coin=%1\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.BCC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.ETC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.QTUM] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.NEO] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.KNC] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.TSL] = arrayOf(
                    Currency.KRW
            )
            CURRENCY_PAIRS[VirtualCurrency.OMG] = arrayOf(
                    Currency.KRW
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBaseLowerCase)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker,
                                           checkerInfo: CheckerInfo) {
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.bid = jsonObject.getDouble("buy")
        ticker.ask = jsonObject.getDouble("sell")
        ticker.last = jsonObject.getDouble("last")
        ticker.vol = jsonObject.getDouble("vol")
        ticker.timestamp = jsonObject.getLong("time") * TimeUtils.MILLIS_IN_SECOND
    }

    @Throws(Exception::class)
    override fun parseErrorFromJsonObject(requestId: Int, jsonObject: JSONObject,
                                          checkerInfo: CheckerInfo?): String? {
        return jsonObject.getString("msg")
    }
}