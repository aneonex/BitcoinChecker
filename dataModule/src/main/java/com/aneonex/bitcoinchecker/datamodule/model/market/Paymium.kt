package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import org.json.JSONObject

class Paymium : Market(NAME, TTS_NAME, getCurrencies()) {
    companion object {
        private const val NAME = "Paymium"
        private const val TTS_NAME = NAME
        private const val URL = "https://paymium.com/api/v1/data/%1\$s/ticker"

        private fun getCurrencies() : CurrencyPairsMap =
            arrayOf(
                "BTC",
            ).associateWithTo(CurrencyPairsMap()) { arrayOf("EUR") }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("price")
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String {
        val stringBuilder = StringBuilder()
        val errorsArray = jsonObject.getJSONArray("errors")
        for (i in 0 until errorsArray.length()) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append("\n")
            }
            stringBuilder.append(errorsArray.getString(i))
        }
        return stringBuilder.toString()
    }
}