package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Indodax : SimpleMarket(
    "Indodax",
    "https://indodax.com/api/pairs",
    "https://indodax.com/api/ticker/%1\$s",
    errorPropertyName = "error_description"
) {
    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        JSONArray(responseString)
            .forEachJSONObject { market ->
                pairs.add(
                    CurrencyPairInfo(
                        market.getString("traded_currency").uppercase(Locale.ROOT), // Base currency
                        market.getString("base_currency")
                            .uppercase(Locale.ROOT), // base_currency is real quoting
                        market.getString("id")
                    )
                )
            }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject.getJSONObject("ticker").apply {
            ticker.bid = getDouble("buy")
            ticker.ask = getDouble("sell")
            ticker.high = getDouble("high")
            ticker.low = getDouble("low")
            ticker.last = getDouble("last")
            ticker.timestamp = getLong("server_time")

            ticker.vol = getDouble("vol_${checkerInfo.currencyBaseLowerCase}")
            ticker.volQuote = getDouble("vol_${checkerInfo.currencyCounterLowerCase}")
        }
    }
}