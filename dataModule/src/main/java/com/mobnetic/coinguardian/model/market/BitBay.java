package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import org.json.JSONObject

class BitBay : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "BitBay.net"
        private const val TTS_NAME = "Bit Bay"
        private const val URL = "https://bitbay.net/API/Public/%1\$s%2\$s/ticker.json"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BCC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.DASH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.GAME] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
            )
            CURRENCY_PAIRS[VirtualCurrency.LSK] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.PLN,
                    Currency.USD,
                    Currency.EUR
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
        ticker.high = jsonObject.getDouble("max")
        ticker.low = jsonObject.getDouble("min")
        ticker.last = jsonObject.getDouble("last")
    }
}