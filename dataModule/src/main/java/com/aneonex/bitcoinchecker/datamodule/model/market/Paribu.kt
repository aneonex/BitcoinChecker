package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

class Paribu : SimpleMarket(
    "Paribu",
    URL,
    URL, // TODO: There is no API for a separate market yet
) {
    companion object {
        private const val URL = "https://v3.paribu.com/app/ticker"
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("data")
            .forEachName { market, _ ->
                val assets = market.uppercase().split('-')
                if(assets.size == 2){
                    pairs.add(CurrencyPairInfo(
                        assets[0],
                        assets[1],
                        null
                    ))
                }
            }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        val market = with(checkerInfo){"$currencyBase-$currencyCounter"}
        val dataJsonObject = jsonObject
            .getJSONObject("data")
            .let { data ->
                data.optJSONObject(market) ?: data.getJSONObject(market.lowercase())
            }

        dataJsonObject.also {
            ticker.bid = it.getDouble("bid")
            ticker.ask = it.getDouble("ask")

            ticker.high = it.getDouble("high")
            ticker.low = it.getDouble("low")

            ticker.vol = it.getDouble("volume")
            ticker.last = it.getDouble("close")
        }
    }
}