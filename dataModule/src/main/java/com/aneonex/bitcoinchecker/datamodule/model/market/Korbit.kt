package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

class Korbit : SimpleMarket(
    "Korbit",
    "https://api.korbit.co.kr/v1/ticker/detailed/all",
    "https://api.korbit.co.kr/v1/ticker/detailed?currency_pair=%1\$s",
) {

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return checkerInfo.currencyPairId ?: with(checkerInfo) { "${currencyBaseLowerCase}_${currencyCounterLowerCase}" }
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject.forEachName { pairId, _ ->
            val assets = pairId.split("_")
            if(assets.size == 2) {
                pairs.add(
                    CurrencyPairInfo(
                        currencyBase = assets[0].uppercase(),
                        currencyCounter = assets[1].uppercase(),
                        pairId
                    )
                )
            }
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bid")
        ticker.ask = jsonObject.getDouble("ask")
        ticker.vol = jsonObject.getDouble("volume")
        ticker.high = jsonObject.getDouble("high")
        ticker.low = jsonObject.getDouble("low")
        ticker.last = jsonObject.getDouble("last")
        ticker.timestamp = jsonObject.getLong("timestamp")
    }
}