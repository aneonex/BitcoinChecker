package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.util.ParseUtils
import com.aneonex.bitcoinchecker.datamodule.util.TimeUtils
import org.json.JSONObject
import java.util.*

class FoscEx : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "Fosc-Ex"
        private const val TTS_NAME = "Fosc Ex"
        private const val URL = "http://www.fosc-ex.com/api-public-ticker"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.KNC] = arrayOf(
                    Currency.KRW
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.vol = ParseUtils.getDoubleFromString(jsonObject, "volume")
        ticker.last = ParseUtils.getDoubleFromString(jsonObject, "last")
        ticker.timestamp = jsonObject.getLong("timestamp") * TimeUtils.MILLIS_IN_SECOND
    }

    @Throws(Exception::class)
    override fun parseErrorFromJsonObject(requestId: Int, jsonObject: JSONObject, checkerInfo: CheckerInfo?): String? {
        return jsonObject.getString("error")
    }
}