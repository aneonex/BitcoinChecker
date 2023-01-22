package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Txbit : SimpleMarket(
        "Txbit",
        "https://api.txbit.io/api/public/getmarkets",
        "https://api.txbit.io/api/public/getticker?market=%1\$s"
        ) {

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONArray("result")
            .forEachJSONObject { market ->
                if(market.getBoolean("IsActive")) {
                    pairs.add(
                        CurrencyPairInfo(
                            market.getString("MarketCurrency"),
                            market.getString("BaseCurrency"),
                            market.getString("MarketName")
                        )
                    )
                }
            }
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject
            .getJSONObject("result")
            .also {
                ticker.last = it.getDouble("Last")

                ticker.bid = it.getDouble("Bid")
                ticker.ask = it.getDouble("Ask")
            }
    }
}