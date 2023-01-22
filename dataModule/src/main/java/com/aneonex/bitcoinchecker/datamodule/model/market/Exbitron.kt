package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

class Exbitron : SimpleMarket(
        "Exbitron",
        "https://www.exbitron.com/api/v2/peatio/public/markets",
        "https://www.exbitron.com/api/v2/peatio/public/markets/%1\$s/tickers"
        ) {

    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString)
            .forEachJSONObject { market ->
                if(market.getString("state") == "enabled") {
                    pairs.add(
                        CurrencyPairInfo(
                            market.getString("base_unit").uppercase(),
                            market.getString("quote_unit").uppercase(),
                            market.getString("symbol")
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
            .also {
                ticker.timestamp = it.getLong("at")
            }
            .getJSONObject("ticker")
            .also {
                ticker.last = it.getDouble("last")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.vol = it.getDouble("amount")
                ticker.volQuote = it.getDouble("vol")
            }
    }
}