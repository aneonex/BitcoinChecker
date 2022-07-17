package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Bitget : SimpleMarket(
        "Bitget",
        "https://api.bitget.com/api/spot/v1/public/products",
        "https://api.bitget.com/api/spot/v1/market/ticker?symbol=%1\$s"
        ) {

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject.getJSONArray("data").forEachJSONObject { market ->
            if(market.getString("status") == "online") {
                pairs.add(
                    CurrencyPairInfo(
                        market.getString("baseCoin"),
                        market.getString("quoteCoin"),
                        market.getString("symbol")
                    )
                )
            }
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONObject("data")
            .let {
                ticker.last = it.getDouble("close")

                ticker.high = it.getDouble("high24h")
                ticker.low = it.getDouble("low24h")

                ticker.vol = it.getDouble("baseVol")
                ticker.volQuote = it.getDouble("quoteVol")

                ticker.bid = it.getDouble("buyOne")
                ticker.ask = it.getDouble("sellOne")

//                ticker.timestamp = it.getLong("ts")
            }
    }
}