package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray

class CoinTree : SimpleMarket(
    "CoinTree",
    "https://trade.cointree.com/api/prices/AUD/change/24h",
    "https://trade.cointree.com/api/prices/AUD/change/24h/?symbols=%1\$s",
    "Coin Tree"
) {
    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString)
            .forEachJSONObject {
                val symbol = it.getString("symbol")
                pairs.add(CurrencyPairInfo(
                    symbol,
                    Currency.AUD,
                    symbol
                ))
            }
    }

    override fun parseTicker(
        requestId: Int,
        responseString: String,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        JSONArray(responseString)
            .getJSONObject(0)
            .also {
                ticker.bid = it.getDouble("bid")
                ticker.ask = it.getDouble("ask")
                ticker.last = it.getDouble("currentRate")
            }
    }
}