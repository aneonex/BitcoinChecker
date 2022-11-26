package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import org.json.JSONArray
import org.json.JSONObject

class Upbit : SimpleMarket(
    "Upbit",
    "https://api.upbit.com/v1/market/all?isDetails=false",
    "https://api.upbit.com/v1/ticker?markets=%1\$s",
    "Up bit"
) {
    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        val markets = JSONArray(responseString)

        for(i in 0 until markets.length()){
            val market = markets.getJSONObject(i)

            val marketId = market.getString("market")
            val assets = marketId.split("-")
            if(assets.size != 2) continue

            pairs.add( CurrencyPairInfo(
                    assets[1],
                    assets[0],
                    marketId,
            ))
        }
    }

    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        val jsonArray = JSONArray(responseString)

        jsonArray.getJSONObject(0).apply {
            ticker.high = getDouble("high_price")
            ticker.low = getDouble("low_price")
            ticker.last = getDouble("trade_price")
            ticker.vol = getDouble("acc_trade_volume")
            ticker.timestamp = getLong("timestamp")
        }
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String? {
        return jsonObject
            .getJSONObject("error")
            .getString("message")
    }
}