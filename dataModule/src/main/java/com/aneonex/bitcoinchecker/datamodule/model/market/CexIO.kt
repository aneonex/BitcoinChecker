package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class CexIO : SimpleMarket(
    "CEX.IO",
    "https://cex.io/api/currency_limits",
    "https://cex.io/api/ticker/%1\$s",
    "CEX IO",
    errorPropertyName = "error"
) {

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return with(checkerInfo) { "$currencyBase/$currencyCounter" }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        if (jsonObject.has("bid")) {
            ticker.bid = jsonObject.getDouble("bid")
        }
        if (jsonObject.has("ask")) {
            ticker.ask = jsonObject.getDouble("ask")
        }
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last")
    }

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject
            .getJSONObject("data")
            .getJSONArray("pairs")
            .forEachJSONObject {
                val currencyBase = it.getString("symbol1")
                val currencyCounter = it.getString("symbol2")
                pairs.add(CurrencyPairInfo(
                    currencyBase,
                    currencyCounter,
                    null))
            }
    }
}