package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.CurrencyPairInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject

class BitcoinVenezuela : Market(NAME, TTS_NAME, null) {
    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.last = responseString.trim { it <= ' ' }.toDouble()
    }

    // ====================
    // Get currency pairs
    // ====================
    override fun getCurrencyPairsUrl(requestId: Int): String? {
        return URL_CURRENCY_PAIRS
    }

    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        parseCurrencyPairsFromCurrencyBase(VirtualCurrency.BTC, jsonObject, pairs)
        parseCurrencyPairsFromCurrencyBase(VirtualCurrency.LTC, jsonObject, pairs)
        parseCurrencyPairsFromCurrencyBase(VirtualCurrency.MSC, jsonObject, pairs)
    }

    @Throws(Exception::class)
    private fun parseCurrencyPairsFromCurrencyBase(currencyBase: String, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo?>) {
        if (!jsonObject.has(currencyBase)) return
        val currencyBaseJsonObject = jsonObject.getJSONObject(currencyBase)
        val counterCurrencyNames = currencyBaseJsonObject.names()
        for (i in 0 until counterCurrencyNames.length()) {
            pairs.add(CurrencyPairInfo(
                    currencyBase,
                    counterCurrencyNames.getString(i),
                    null))
        }
    }

    companion object {
        private const val NAME = "BitcoinVenezuela"
        private const val TTS_NAME = "Bitcoin Venezuela"
        private const val URL = "https://api.bitcoinvenezuela.com/?html=no&currency=%1\$s&amount=1&to=%2\$s"
        private const val URL_CURRENCY_PAIRS = "https://api.bitcoinvenezuela.com/"
    }
}