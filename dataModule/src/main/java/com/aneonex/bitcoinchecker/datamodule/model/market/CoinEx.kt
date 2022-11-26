package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

class CoinEx : SimpleMarket(
    "CoinEx",
    "https://api.coinex.com/v1/market/info",
    "https://api.coinex.com/v1/market/ticker?market=%1\$s",
    "Coin ex"
) {

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("data")
            .forEachName { _, market ->
                pairs.add(
                    CurrencyPairInfo(
                        market.getString("trading_name"),
                        market.getString("pricing_name"),
                        market.getString("name"),
                    )
                )
            }
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject
            .getJSONObject("data")
            .also {
                ticker.timestamp = it.getLong("date")
            }
            .getJSONObject("ticker")
            .also {
                ticker.bid = it.getDouble("buy")
                ticker.ask = it.getDouble("sell")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")

                ticker.last = it.getDouble("last")
                ticker.vol = it.getDouble("vol")
            }
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String {
        val code = jsonObject.getInt("code")
        val message = jsonObject.getString("message")

        return "$message (code: $code)"
    }
}