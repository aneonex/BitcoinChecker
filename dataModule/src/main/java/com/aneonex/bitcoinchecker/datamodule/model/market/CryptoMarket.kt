package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.TimeUtils
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

class CryptoMarket : SimpleMarket(
        "CryptoMarket",
        "https://api.exchange.cryptomkt.com/api/3/public/symbol",
        "https://api.exchange.cryptomkt.com/api/3/public/ticker/%1\$s",
        "Crypto Market"
        ) {

        override fun parseCurrencyPairsFromJsonObject(
            requestId: Int,
            jsonObject: JSONObject,
            pairs: MutableList<CurrencyPairInfo>
        ) {
            jsonObject.forEachName { market, item ->
                pairs.add(
                    CurrencyPairInfo(
                        item.getString("base_currency"),
                        item.getString("quote_currency"),
                        market
                    )
                )
            }
        }


    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .also {
                ticker.timestamp = TimeUtils.convertISODateToTimestamp(it.getString("timestamp"))

                ticker.last = it.getDouble("last")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.vol = it.getDouble("volume")
                ticker.volQuote = it.getDouble("volume_quote")

                ticker.bid = it.getDouble("bid")
                ticker.ask = it.getDouble("ask")
            }
    }
}