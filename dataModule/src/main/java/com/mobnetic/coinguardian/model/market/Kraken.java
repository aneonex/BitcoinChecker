package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject

class Kraken : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return if (checkerInfo.currencyPairId != null) {
            String.format(URL, checkerInfo.currencyPairId)
        } else {
            val currencyBase = fixCurrency(checkerInfo.currencyBase)
            val currencyCounter = fixCurrency(checkerInfo.currencyCounter)
            String.format(URL, currencyBase + currencyCounter)
        }
    }

    private fun fixCurrency(currency: String?): String? {
        if (VirtualCurrency.BTC == currency) return VirtualCurrency.XBT
        if (VirtualCurrency.VEN == currency) return VirtualCurrency.XVN
        return if (VirtualCurrency.DOGE == currency) VirtualCurrency.XDG else currency
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val resultObject = jsonObject.getJSONObject("result")
        val pairObject = resultObject.getJSONObject(resultObject.names().getString(0))
        ticker.bid = getDoubleFromJsonArrayObject(pairObject, "b")
        ticker.ask = getDoubleFromJsonArrayObject(pairObject, "a")
        ticker.vol = getDoubleFromJsonArrayObject(pairObject, "v")
        ticker.high = getDoubleFromJsonArrayObject(pairObject, "h")
        ticker.low = getDoubleFromJsonArrayObject(pairObject, "l")
        ticker.last = getDoubleFromJsonArrayObject(pairObject, "c")
    }

    @Throws(Exception::class)
    private fun getDoubleFromJsonArrayObject(jsonObject: JSONObject, arrayKey: String): Double {
        val jsonArray = jsonObject.getJSONArray(arrayKey)
        return if (jsonArray != null && jsonArray.length() > 0) jsonArray.getDouble(0) else 0.0
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        val result = jsonObject.getJSONObject("result")
        val pairNames = result.names()
        for (i in 0 until pairNames.length()) {
            val pairId = pairNames.getString(i)
            if (pairId != null && pairId.indexOf('.') == -1) {
                val pairJsonObject = result.getJSONObject(pairId)
                pairs.add(CurrencyPairInfo(
                        parseCurrency(pairJsonObject.getString("base")),
                        parseCurrency(pairJsonObject.getString("quote")),
                        pairId))
            }
        }
    }

    private fun parseCurrency(currency: String): String {
        var currency = currency
        if (currency != null && currency.length >= 2) {
            val firstChar = currency[0]
            if (firstChar == 'Z' || firstChar == 'X') {
                currency = currency.substring(1)
            }
        }
        if (VirtualCurrency.XBT == currency) return VirtualCurrency.BTC
        if (VirtualCurrency.XVN == currency) return VirtualCurrency.VEN
        return if (VirtualCurrency.XDG == currency) VirtualCurrency.DOGE else currency
    }

    companion object {
        private const val NAME = "Kraken"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.kraken.com/0/public/Ticker?pair=%1\$s"
        private const val URL_CURRENCY_PAIRS = "https://api.kraken.com/0/public/AssetPairs"
    }
}