package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class Woo : SimpleMarket(
        "WOO",
        "https://api.woo.org/v1/public/info",
        "https://api.woo.org/v1/public/market_trades?symbol=%1\$s&limit=1"
        ) {

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        if(checkerInfo.contractType != FuturesContractType.NONE)
            return String.format("https://api.woo.org/v1/public/futures/%1\$s", getPairId(checkerInfo))

        return super.getUrl(requestId, checkerInfo)
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONArray("rows")
            .forEachJSONObject { market ->
                val pair = parseCurrencyPair(market)
                if(pair != null) {
                    pairs.add(pair)
                }
            }
    }

    private fun parseCurrencyPair(market: JSONObject): CurrencyPairInfo? {
        val symbol = market.getString("symbol")
        val symbolParts = symbol.split('_')
        if(symbolParts.size != 3)
            return null

        val contractType = when(symbolParts[0]) {
            "SPOT" -> FuturesContractType.NONE
            "PERP" -> FuturesContractType.PERPETUAL
            else -> return null
        }

        return CurrencyPairInfo(
            symbolParts[1],
            symbolParts[2],
            symbol,
            contractType
        )
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        if(checkerInfo.contractType == FuturesContractType.NONE) {
            jsonObject
                .getJSONArray("rows")
                .getJSONObject(0)
                .also {
                    ticker.last = it.getDouble("executed_price")
                    ticker.timestamp = it.getString("executed_timestamp")
                        .replace(".", "")
                        .toLong()
                }
        } else {
            jsonObject
                .also {
                    ticker.timestamp = it.getLong("timestamp")
                }
                .getJSONObject("info")
                .also {
                    ticker.last = it.getDouble("24h_close")

                    ticker.high = it.getDouble("24h_high")
                    ticker.low = it.getDouble("24h_low")

                    ticker.vol = it.getDouble("24h_volumn")
                    ticker.volQuote = it.getDouble("24h_amount")
                }
        }
    }
}