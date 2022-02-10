package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

class SafeTrade : SimpleMarket(
        "SafeTrade",
        "https://safe.trade/api/v2/peatio/public/markets",
        "https://safe.trade/api/v2/peatio/public/markets/%1\$s/tickers",
        "Safe trade"
        ) {

    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString).forEachJSONObject { market ->
            pairs.add(CurrencyPairInfo(
                market.getString("base_unit").uppercase(),
                market.getString("quote_unit").uppercase(),
                market.getString("id")
            ))
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.timestamp = jsonObject.getLong("at")

        jsonObject
            .getJSONObject("ticker")
            .let {
                ticker.last = it.getDouble("last")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.vol = it.getDouble("vol") // Exchange has inverted volume
                ticker.volQuote = it.getDouble("volume")

                ticker.bid = it.getDouble("buy")
                ticker.ask = it.getDouble("sell")
            }
    }
}