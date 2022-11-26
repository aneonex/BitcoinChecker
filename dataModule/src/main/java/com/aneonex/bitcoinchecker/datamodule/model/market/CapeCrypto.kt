package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONArray
import org.json.JSONObject


class CapeCrypto : Market(NAME, TTS_NAME) {
    companion object {
        private const val NAME = "Cape Crypto"
        private const val TTS_NAME = NAME
        private const val URL_PAIRS = "https://trade.capecrypto.com/api/v2/peatio/public/markets"
        private const val URL_TICKER = "https://trade.capecrypto.com/api/v2/peatio/public/markets/%1\$s/tickers"
        private const val URL_ORDERS = "https://trade.capecrypto.com/api/v2/peatio/public/markets/%1\$s/order-book?asks_limit=1&bids_limit=1"
    }

    override fun getCurrencyPairsUrl(requestId: Int): String {
        return URL_PAIRS
    }

    override fun parseCurrencyPairs(
        requestId: Int,
        responseString: String,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        JSONArray(responseString)
            .forEachJSONObject { market ->
                pairs.add(
                    CurrencyPairInfo(
                        market.getString("base_unit").uppercase(),
                        market.getString("quote_unit").uppercase(),
                        market.getString("id")
                    )
                )
            }
    }

    override fun getNumOfRequests(checkerInfo: CheckerInfo?): Int {
        return 2
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        val pairId = checkerInfo.currencyPairId ?: with(checkerInfo){"$currencyBaseLowerCase$currencyCounterLowerCase"}
        return if (requestId == 0) {
            String.format(URL_TICKER, pairId)
        } else {
            String.format(URL_ORDERS, pairId)
        }
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {

        if (requestId == 0) {
            ticker.timestamp = jsonObject.getLong("at")

            jsonObject.getJSONObject("ticker").also {
                ticker.last = it.getDouble("last")

                ticker.vol = it.getDouble("amount")
                ticker.volQuote = it.getDouble("vol")

                ticker.high = it.getDouble("high")
                ticker.low = it.getDouble("low")
            }
        } else {
            jsonObject
                .getJSONArray("bids")
                .getJSONObject(0)
                .also { ticker.bid = it.getDouble("price") }

            jsonObject
                .getJSONArray("asks")
                .getJSONObject(0)
                .also { ticker.ask = it.getDouble("price") }
        }
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String? {
        return jsonObject.getJSONArray("errors").getString(0)
    }
}
