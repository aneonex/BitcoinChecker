package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

class Bleutrade : SimpleMarket(
    "Bleutrade",
    "https://bleutrade.com/api/v3/public/getmarkets",
    "https://bleutrade.com/api/v3/public/getticker?market=%1\$s",
    errorPropertyName = "message"
) {
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val jsonTicker: JSONObject =
            jsonObject["result"].let {
                if (it is JSONArray) it.getJSONObject(0)
                else it as JSONObject
            }
        ticker.bid = jsonTicker.getDouble("Bid")
        ticker.ask = jsonTicker.getDouble("Ask")
        ticker.last = jsonTicker.getDouble("Last")
    }

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject
            .getJSONArray("result")
            .forEachJSONObject {
                pairs.add(CurrencyPairInfo(
                    it.getString("MarketAsset"),
                    it.getString("BaseAsset"),
                    it.getString("MarketName")
                ))
            }
    }
}