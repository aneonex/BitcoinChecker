package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

class Bittrex : SimpleMarket(
    "Bittrex",
    "https://api.bittrex.com/v3/markets",
    "https://api.bittrex.com/v3/markets/%1\$s/ticker"
) {
    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString)
            .forEachJSONObject {
                pairs.add(
                    CurrencyPairInfo(
                        it.getString("baseCurrencySymbol"),
                        it.getString("quoteCurrencySymbol"),
                        null // it.getString("symbol")
                    )
                )
            }
    }

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return with(checkerInfo){
            "$currencyBase-$currencyCounter"
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject.let {
            ticker.bid = it.getDouble("bidRate")
            ticker.ask = it.getDouble("askRate")
            ticker.last = it.getDouble("lastTradeRate")
        }
    }
}