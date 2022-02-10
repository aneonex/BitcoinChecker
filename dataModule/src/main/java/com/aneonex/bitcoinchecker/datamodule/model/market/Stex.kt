package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Stex : SimpleMarket(
        "STEX",
        "https://api3.stex.com/public/currency_pairs/list/ALL",
        "https://api3.stex.com/public/ticker/%1\$s"
        ) {

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject.getJSONArray("data").forEachJSONObject { market ->
            pairs.add(CurrencyPairInfo(
                market.getString("currency_code"),
                market.getString("market_code"),
                market.getString("id")
            ))
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONObject("data")
            .let {
                ticker.last = it.getDouble("last")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.vol = it.getDouble("volumeQuote") // Exchange has inverted volume
                ticker.volQuote = it.getDouble("volume")

                ticker.bid = it.getDouble("bid")
                ticker.ask = it.getDouble("ask")

                ticker.timestamp = it.getLong("timestamp")
            }
    }
}