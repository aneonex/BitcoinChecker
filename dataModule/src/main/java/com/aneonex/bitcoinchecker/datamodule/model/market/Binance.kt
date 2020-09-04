package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import org.json.JSONObject

class Binance : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyPairId)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bidPrice")
        ticker.ask = jsonObject.getDouble("askPrice")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("highPrice")
        ticker.low = jsonObject.getDouble("lowPrice")
        ticker.last = jsonObject.getDouble("lastPrice")
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        val jsonObject = JSONObject(responseString)
        val jsonSymbols = jsonObject.getJSONArray("symbols")
        for (i in 0 until jsonSymbols.length()) {
            val marketJsonObject = jsonSymbols.getJSONObject(i)
            val status = marketJsonObject.getString("status")
            if (status != "TRADING") {
                continue
            }
            val symbol = marketJsonObject.getString("symbol")
            val baseAsset = marketJsonObject.getString("baseAsset")
            val quoteAsset = marketJsonObject.getString("quoteAsset")
            pairs.add(CurrencyPairInfo(
                    baseAsset,
                    quoteAsset,
                    symbol))
        }
    }

    companion object {
        private const val NAME = "Binance"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.binance.com/api/v3/ticker/24hr?symbol=%1\$s"
        private const val URL_CURRENCY_PAIRS = "https://api.binance.com/api/v3/exchangeInfo"
        private val COUNTER_CURRENCIES = arrayOf(
                VirtualCurrency.BNB,
                VirtualCurrency.BTC,
                VirtualCurrency.ETH,
                VirtualCurrency.USDT
        )
    }
}