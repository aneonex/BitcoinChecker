package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

// API: https://www.bitstamp.net/api/
class Bitstamp : SimpleMarket(
    "Bitstamp",
    "https://www.bitstamp.net/api/v2/trading-pairs-info/",
    "https://www.bitstamp.net/api/v2/ticker/%1\$s"
) {

    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString)
            .forEachJSONObject { market ->
                if (market.getString("trading") == "Enabled") {
                    val assetsInPair = market
                        .getString("name")
                        .split('/')

                    if (assetsInPair.size == 2) {
                        pairs.add(
                            CurrencyPairInfo(
                                assetsInPair[0], // base
                                assetsInPair[1], // quote
                                market.getString("url_symbol")
                            )
                        )
                    }
                }
            }
    }

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return super.getPairId(checkerInfo) ?: (checkerInfo.currencyBaseLowerCase + checkerInfo.currencyCounterLowerCase)
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")

        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")

        ticker.vol = jsonObject.getDouble("volume")
        ticker.last = jsonObject.getDouble("last")
        ticker.timestamp = jsonObject.getLong("timestamp")
    }
}