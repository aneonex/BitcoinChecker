package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Bybit : SimpleMarket(
    "Bybit (Spot)",
    "https://api.bybit.com/spot/v3/public/symbols",
    "https://api.bybit.com/spot/v3/public/quote/ticker/24hr?symbol=%1\$s",
    errorPropertyName = "retMsg"
) {
    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("result")
            .getJSONArray("list")
            .forEachJSONObject { market ->
                if(market.optString("showStatus") == "1") {
                    pairs.add(
                        CurrencyPairInfo(
                            market.getString("baseCoin"),
                            market.getString("quoteCoin"),
                            market.getString("name")
                        )
                    )
                }
            }
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject
            .getJSONObject("result")
            .apply {
                ticker.ask = getDouble("ap")
                ticker.bid = getDouble("bp")

                ticker.high = getDouble("h")
                ticker.low = getDouble("l")

                ticker.last = getDouble("lp")

                ticker.vol = getDouble("v")
                ticker.volQuote = getDouble("qv")

                ticker.timestamp = getLong("t")
            }
    }
}