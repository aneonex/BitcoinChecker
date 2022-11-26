package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject

// API doc: https://www.tokocrypto.com/apidocs/
class Tokocrypto : SimpleMarket(
        "Tokocrypto",
        "https://www.tokocrypto.com/open/v1/common/symbols",
        "https://api.binance.me/api/v3/klines?symbol=%1\$s&interval=1d&limit=1",
        "Toko crypto",
        errorPropertyName = "msg"
) {

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("data")
            .getJSONArray("list")
            .forEachJSONObject { market ->
                if (market.getInt("type") == 1) {
                    pairs.add(
                        CurrencyPairInfo(
                            market.getString("baseAsset"),
                            market.getString("quoteAsset"),
                            market.getString("symbol")
                        )
                    )
                }
            }
    }

    override fun getPairId(checkerInfo: CheckerInfo): String? {
        // https://www.tokocrypto.com/apidocs/#compressedaggregate-trades-list
        // Symbol: when symbol type is 1, replace _ of symbol with null string
        return checkerInfo.currencyPairId?.replace("_", "")
    }

    override fun parseTicker(
        requestId: Int,
        responseString: String,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        JSONArray(responseString)
            .getJSONArray(0)
            .let {
                ticker.last = it.getDouble(4)

                ticker.high = it.getDouble(2)
                ticker.low = it.getDouble(3)

                ticker.vol = it.getDouble(5)
                ticker.volQuote = it.getDouble(7)
            }
    }
}