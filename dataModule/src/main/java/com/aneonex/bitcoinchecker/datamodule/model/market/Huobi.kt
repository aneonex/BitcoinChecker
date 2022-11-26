package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Huobi : SimpleMarket(
    "Huobi",
    "https://api.huobi.pro/v2/settings/common/symbols",
    "https://api.huobi.pro/market/detail/merged?symbol=%1\$s",
    errorPropertyName = "err-msg"
) {

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return checkerInfo.currencyPairId
            ?: (checkerInfo.currencyBaseLowerCase + checkerInfo.currencyCounterLowerCase)
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        if ("ok".equals(jsonObject.getString("status"), ignoreCase = true)) {
            jsonObject.getJSONArray("data")
                .forEachJSONObject { market ->
                    // if trading is enabled
                    if(market.getBoolean("te")) {
                        pairs.add(
                            CurrencyPairInfo(
                                market.getString("bcdn"),
                                market.getString("qcdn"),
                                market.getString("sc")
                            )
                        )
                    }
                }
        } else {
            throw Exception("Parse currency pairs error.")
        }
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject.getJSONObject("tick").also {
            ticker.bid = it.getJSONArray("bid").getDouble(0)
            ticker.ask = it.getJSONArray("ask").getDouble(0)
            ticker.vol = it.getDouble("amount")
            ticker.volQuote = it.getDouble("vol")
            ticker.high = it.getDouble("high")
            ticker.low = it.getDouble("low")
            ticker.last = it.getDouble("close")
        }
    }
}