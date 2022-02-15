package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import org.json.JSONObject

class BitBay : SimpleMarket(
        "Zonda (BitBay)",
        "https://api.zonda.exchange/rest/trading/ticker",
        "https://api.zonda.exchange/rest/trading/ticker/%1\$s",
        "Zonda"
        ) {

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return super.getPairId(checkerInfo) ?: with(checkerInfo) { "${currencyBase}-${currencyCounter}" }
    }

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        val itemsJson = jsonObject.getJSONObject("items")
        itemsJson.keys().forEach { pairId ->
            val coins = pairId.split('-')
            if(coins.size == 2){
                pairs.add( CurrencyPairInfo(
                    coins[0], // base
                    coins[1], // quote
                    pairId
                ))
            }
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONObject("ticker")
            .let {
                ticker.timestamp = it.getLong("time")
                ticker.last = it.getDouble("rate")

                ticker.bid = it.getDouble("highestBid")
                ticker.ask = it.getDouble("lowestAsk")
            }
    }
}