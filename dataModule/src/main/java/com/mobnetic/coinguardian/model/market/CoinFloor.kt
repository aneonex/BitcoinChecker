package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.Ticker
import com.mobnetic.coinguardian.model.currency.Currency
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import org.json.JSONObject
import java.util.*

class CoinFloor : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Coinfloor"
        private const val TTS_NAME = "Coin Floor"
        private const val URL = "https://webapi.coinfloor.co.uk:8090/bist/%1\$s/%2\$s/ticker/"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.XBT] = arrayOf(
                    Currency.GBP,
                    Currency.EUR,
                    Currency.USD
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    Currency.GBP
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
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last")
    }
}