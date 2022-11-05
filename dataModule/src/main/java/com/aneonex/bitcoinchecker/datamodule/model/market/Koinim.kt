package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import org.json.JSONObject

class Koinim : Market(NAME, TTS_NAME, getCurrencyPairs()) {
    companion object {
        private const val NAME = "Koinim"
        private const val TTS_NAME = NAME
        private const val URL = "https://koinim.com/api/v1/ticker/%1\$s/"

        private fun getCurrencyPairs(): CurrencyPairsMap {
            val baseCurrencies = arrayOf(
                "BTC",
                "LTC",
                "BCH",
                "ETH",
                "DOGE",
                "DASH",
            )

            val quoteCurrencies = arrayOf(Currency.TRY)
            return baseCurrencies.associateWithTo(CurrencyPairsMap()) { quoteCurrencies }
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return with(checkerInfo) { String.format(URL, "${currencyBase}_$currencyCounter") }
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")

        ticker.vol = jsonObject.getDouble("volume")

        ticker.high = jsonObject.optDouble("high", ticker.high)
        ticker.low = jsonObject.optDouble("low", ticker.low)

        ticker.last = jsonObject.getDouble("last_order")
    }
}