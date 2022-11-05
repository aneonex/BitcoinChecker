// @joseccnet contribution.
package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.ParseUtils
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

class Uphold : SimpleMarket(
    "Uphold",
    "https://api.uphold.com/v0/ticker",
    "https://api.uphold.com/v0/ticker/%1\$s",
) {
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = ParseUtils.getDoubleFromString(jsonObject, "bid")
        ticker.ask = ParseUtils.getDoubleFromString(jsonObject, "ask")
        ticker.last = (ticker.bid + ticker.ask) / 2 //This is how Uphold operate on production (as I observed)
    }

    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        JSONArray(responseString).forEachJSONObject { jsonPair ->
            val pairId = jsonPair.getString("pair")
            if(!pairId.startsWith('$')) {
                val quoteCurrency = jsonPair.getString("currency")
                if (pairId.endsWith(quoteCurrency)) {
                    val baseCurrency: String = pairId
                        .removeSuffix(quoteCurrency)
                        .removeSuffix("-")

                    if (quoteCurrency != baseCurrency) {
                        pairs.add(
                            CurrencyPairInfo(
                                baseCurrency,
                                quoteCurrency,
                                pairId
                            )
                        )
                    }
                }
            }
        }
    }
}