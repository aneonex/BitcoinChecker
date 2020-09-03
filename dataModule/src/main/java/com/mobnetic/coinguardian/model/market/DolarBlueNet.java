package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import org.json.JSONObject
import java.util.*

class DolarBlueNet : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "DolarBlue.net"
        private const val TTS_NAME = "Dolar Blue"
        private const val URL = "http://dolar.bitplanet.info/api.php"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[Currency.USD] = arrayOf(
                    Currency.ARS
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.ask = jsonObject.getDouble("venta")
        ticker.bid = jsonObject.getDouble("compra")
        ticker.last = ticker.ask
    }
}