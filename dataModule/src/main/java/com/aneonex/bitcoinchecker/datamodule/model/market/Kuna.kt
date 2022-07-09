package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.exceptions.MarketParseException
import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray

class Kuna : SimpleMarket(
        "Kuna",
        "https://api.kuna.io/v3/markets",
        "https://api.kuna.io/v3/tickers?symbols=%1\$s",
        ) {

    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString).forEachJSONObject {
            pairs.add(
                CurrencyPairInfo(
                    it.getString("base_unit").uppercase(),
                    it.getString("quote_unit").uppercase(),
                    it.getString("id")
                )
            )
        }
    }

    override fun parseTicker(
        requestId: Int,
        responseString: String,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        val dataJson = JSONArray(responseString)
        if(dataJson.length() != 1)
            throw MarketParseException("No data")

        dataJson.getJSONArray(0)
            .let {
                ticker.bid = it.getDouble(1)
                ticker.ask = it.getDouble(3)

                ticker.last = it.getDouble(7)
                ticker.vol = it.getDouble(8)

                ticker.high = it.getDouble(9)
                ticker.low = it.getDouble(10)
            }
    }
}