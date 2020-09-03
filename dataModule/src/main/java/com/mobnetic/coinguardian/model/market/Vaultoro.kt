package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import java.util.*

class Vaultoro : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Vaultoro"
        private const val TTS_NAME = NAME
        private const val URL = "https://api.vaultoro.com/latest/"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[Currency.GOLD] = arrayOf(
                    VirtualCurrency.BTC
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.last = responseString.toDouble()
    }
}