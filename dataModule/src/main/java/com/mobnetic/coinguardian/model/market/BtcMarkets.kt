package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import org.json.JSONObject

class BtcMarkets : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "BtcMarkets.net"
        private const val TTS_NAME = "BTC Markets net"
        private const val URL = "https://api.btcmarkets.net/market/%1\$s/%2\$s/tick"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.XRP] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bestBid")
        ticker.ask = jsonObject.getDouble("bestAsk")
        ticker.last = jsonObject.getDouble("lastPrice")
        ticker.timestamp = jsonObject.getLong("timestamp")
    }
}