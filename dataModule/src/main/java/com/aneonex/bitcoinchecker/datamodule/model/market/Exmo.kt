package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

class Exmo : SimpleMarket(
    "Exmo",
    "https://api.exmo.com/v1.1/pair_settings/",
    "https://api.exmo.com/v1.1/ticker/", // Does not support symbol
    errorPropertyName = "error"
) {
    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject
            .getJSONObject(checkerInfo.currencyPairId!!)
            .also {
                ticker.bid = it.getDouble("buy_price")
                ticker.ask = it.getDouble("sell_price")

                ticker.vol = it.getDouble("vol")
                ticker.volQuote = it.getDouble("vol_curr")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.last = it.getDouble("last_trade")
            }
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .forEachName { pairId, _ ->
                val currencies = pairId.split("_")
                if (currencies.size == 2) {
                    pairs.add(
                        CurrencyPairInfo(
                            currencies[0],
                            currencies[1],
                            pairId
                        )
                    )
                }
            }
    }
}