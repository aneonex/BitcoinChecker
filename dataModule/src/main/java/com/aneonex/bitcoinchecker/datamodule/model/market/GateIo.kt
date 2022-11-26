package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.exceptions.MarketParseException
import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray

class GateIo : SimpleMarket(
    "Gate.io",
    "https://api.gateio.ws/api/v4/spot/currency_pairs",
    "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=%1\$s",
    "Gate io",
    errorPropertyName = "message"
) {
    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        JSONArray(responseString)
            .forEachJSONObject { pairJson ->
                if(pairJson.getString("trade_status") == "tradable") {
                    pairs.add(
                        CurrencyPairInfo(
                            pairJson.getString("base"),
                            pairJson.getString("quote"),
                            pairJson.getString("id")
                        )
                    )
                }
            }
    }

    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        val jsonArray = JSONArray(responseString)
        if(jsonArray.length() < 1) throw MarketParseException("No data")

        val jsonObject = jsonArray.getJSONObject(0)

        ticker.bid = jsonObject.getDouble("highest_bid")
        ticker.ask = jsonObject.getDouble("lowest_ask")

        ticker.vol = jsonObject.getDouble("base_volume")
        ticker.volQuote = jsonObject.getDouble("quote_volume")

        ticker.high = jsonObject.getDouble("high_24h")
        ticker.low = jsonObject.getDouble("low_24h")
        ticker.last = jsonObject.getDouble("last")
    }
}