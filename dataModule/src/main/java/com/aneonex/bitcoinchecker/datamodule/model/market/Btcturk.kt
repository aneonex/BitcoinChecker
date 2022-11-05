package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.TimeUtils
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Btcturk : SimpleMarket(
    "BtcTurk",
    "https://api.btcturk.com/api/v2/ticker",
    "https://api.btcturk.com/api/v2/ticker?pairSymbol=%1\$s",
    "Btc Turk",
) {
    override fun parseCurrencyPairs(requestId: Int, responseString: String, pairs: MutableList<CurrencyPairInfo>) {
        JSONObject(responseString).getJSONArray("data")
            .forEachJSONObject { pairJson ->
                pairs.add(
                    CurrencyPairInfo(
                        pairJson.getString("numeratorSymbol"),
                        pairJson.getString("denominatorSymbol"),
                        pairJson.getString("pairNormalized")
                    )
                )
            }
    }

    override fun getPairId(checkerInfo: CheckerInfo): String =
        checkerInfo.currencyPairId ?: "${checkerInfo.currencyBase}_${checkerInfo.currencyCounter}"

    override fun parseTicker(requestId: Int, responseString: String, ticker: Ticker, checkerInfo: CheckerInfo) {
        JSONObject(responseString)
            .getJSONArray("data")
            .getJSONObject(0).also {
                ticker.bid = it.getDouble("bid")
                ticker.ask = it.getDouble("ask")
                ticker.vol = it.getDouble("volume")
                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")
                ticker.last = it.getDouble("last")
                ticker.timestamp = (it.getDouble("timestamp") * TimeUtils.MILLIS_IN_SECOND).toLong()
            }
    }
}