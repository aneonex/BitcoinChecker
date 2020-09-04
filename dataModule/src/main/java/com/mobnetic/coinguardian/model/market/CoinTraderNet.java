package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class CoinTraderNet : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "CoinTrader.net"
        private const val TTS_NAME = "Coin Trader"
        private const val URL = "https://www.cointrader.net/api4/stats/daily/%1\$s%2\$s"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.USD,
                    Currency.CAD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val dataJsonObject = jsonObject.getJSONObject("data")
        val dataNamesArray = dataJsonObject.names()
        val tickerJsonObject = dataJsonObject.getJSONObject(dataNamesArray.getString(0))
        ticker.bid = tickerJsonObject.getDouble("bid")
        ticker.ask = tickerJsonObject.getDouble("offer")
        ticker.vol = tickerJsonObject.getDouble("volume")
        ticker.high = tickerJsonObject.getDouble("high")
        ticker.low = tickerJsonObject.getDouble("low")
        ticker.last = tickerJsonObject.getDouble("lastTradePrice")
    }
}