package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class XTCom : SimpleMarket(
        "XT.COM",
        "https://sapi.xt.com/v4/public/ticker/price",
        "https://sapi.xt.com/v4/public/ticker/24h?symbol=%1\$s",
        ) {

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject
            .getJSONArray("result")
            .forEachJSONObject { symbolJson ->
                val pairId = symbolJson.getString("s")
                val currencies = pairId.uppercase().split('_')
                if(currencies.size == 2) {
                    pairs.add(
                        CurrencyPairInfo(
                            currencies[0].uppercase(),
                            currencies[1].uppercase(),
                            pairId,
                        )
                    )
                }

            }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONArray("result")
            .getJSONObject(0)
            .let {
                ticker.last = it.getDouble("c")

                ticker.high = it.getDouble("h")
                ticker.low = it.getDouble("l")

                ticker.vol = it.getDouble("v")

                ticker.timestamp = it.getLong("t")
            }
    }
}