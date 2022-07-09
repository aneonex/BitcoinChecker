package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import org.json.JSONObject
import java.util.*

class XTCom : SimpleMarket(
        "XT.COM",
        "https://api.xt.com/data/api/v1/getTickers",
        "https://api.xt.com/data/api/v1/getTicker?market=%1\$s",
        ) {

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject.keys().forEach { pair ->
            val currencies = pair.split('_')
            if(currencies.size == 2) {
                pairs.add(
                    CurrencyPairInfo(
                        currencies[0].uppercase(Locale.ROOT),
                        currencies[1].uppercase(Locale.ROOT),
                        pair,
                    )
                )
            }
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .let {
                ticker.last = it.getDouble("price")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")


                ticker.bid = it.getDouble("bid")
                ticker.ask = it.getDouble("ask")

//                ticker.vol = it.getDouble("coinVol")
                ticker.volQuote = it.getDouble("moneyVol")
            }
    }
}