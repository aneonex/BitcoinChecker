package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Kucoin : SimpleMarket(
    "KuCoin",
    "https://api.kucoin.com/api/v2/symbols",
    "https://api.kucoin.com/api/v1/market/stats?symbol=%1\$s"
) {

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject.getJSONObject("data").also {
            ticker.bid = it.getDouble("buy")
            ticker.ask = it.getDouble("sell")

            ticker.vol = it.getDouble("vol")
            ticker.volQuote = it.getDouble("volValue")

            ticker.high = it.getDouble("high")
            ticker.low = it.getDouble("low")

            ticker.last = it.getDouble("last")
            ticker.timestamp = it.getLong("time")
        }
    }

    // ====================
    // Get currency pairs
    // ====================
    @Throws(Exception::class)
    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONArray("data")
            .forEachJSONObject { symbolData ->
                if (symbolData.getBoolean("enableTrading")) {
                    pairs.add(
                        CurrencyPairInfo(
                            symbolData.getString("baseCurrency"),
                            symbolData.getString("quoteCurrency"),
                            symbolData.getString("symbol")
                        )
                    )
                }
            }
    }
}