package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class OkexFutures : SimpleMarket(
    "OKX Futures",
    "https://www.okx.com/api/v5/market/tickers?instType=SWAP",
    "https://www.okx.com/api/v5/market/ticker?instId=%1\$s"
) {

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONArray("data")
            .forEachJSONObject {
                val pairId = it.getString("instId")
                val assets = pairId.split('-')

                if(assets.size == 3 && assets[2] == "SWAP") {
                    pairs.add(CurrencyPairInfo(
                        assets[0],
                        assets[1],
                        pairId,
                        FuturesContractType.PERPETUAL
                    ))
                }
            }
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONArray("data")
            .getJSONObject(0)
            .also {
                ticker.bid = it.getDouble("bidPx")
                ticker.ask = it.getDouble("askPx")

                ticker.vol = it.getDouble("vol24h")
                ticker.volQuote = it.getDouble("volCcy24h")

                ticker.high = it.getDouble("high24h")
                ticker.low = it.getDouble("low24h")

                ticker.last = it.getDouble("last")
                ticker.timestamp =  it.getLong("ts")
            }
    }
}