package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.*
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import com.aneonex.bitcoinchecker.datamodule.util.optDoubleNoData
import org.json.JSONObject

class Deribit : Market(NAME, TTS_NAME) {

    override val currencyPairsNumOfRequests: Int
        get() = ROOT_CURRENCIES.size

    override fun getCurrencyPairsUrl(requestId: Int): String {
        return String.format(URL_CURRENCY_PAIRS, ROOT_CURRENCIES[requestId])
    }

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        jsonObject
            .getJSONArray("result")
            .forEachJSONObject{ jsonInstrument ->

                val contractType: FuturesContractType? = when(jsonInstrument.getString("settlement_period")) {
                    "perpetual" -> FuturesContractType.PERPETUAL
                    // "month" -> FuturesContractType.MONTHLY
                    // "week" -> FuturesContractType.WEEKLY
                    else -> null
                }

                if(contractType != null) {
                    pairs.add(CurrencyPairInfo(
                        jsonInstrument.getString("base_currency"),
                        jsonInstrument.getString("quote_currency"),
                        jsonInstrument.getString("instrument_name"),
                        contractType
                        )
                    )
                }
            }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyPairId)
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject
            .getJSONObject("result")
                .also {
                    ticker.last = it.getDouble("last_price")

                    ticker.bid = it.getDouble("best_bid_price")
                    ticker.ask = it.getDouble("best_ask_price")

                    ticker.timestamp = it.getLong("timestamp")
                }
            .getJSONObject("stats")
                .also {
                    ticker.vol = it.optDoubleNoData("volume")

                    ticker.high = it.optDoubleNoData("high")
                    ticker.low = it.optDoubleNoData("low")
                }
    }

    companion object {
        private const val NAME = "Deribit"
        private const val TTS_NAME = NAME
        private const val URL = "https://www.deribit.com/api/v2/public/ticker?instrument_name=%1\$s"
        private const val URL_CURRENCY_PAIRS = "https://www.deribit.com/api/v2/public/get_instruments?currency=%1\$s&expired=false&kind=future"

        private val ROOT_CURRENCIES = listOf("BTC", "ETH", "SOL", "USDC")
    }
}