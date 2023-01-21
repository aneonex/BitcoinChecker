package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class BybitDerivatives : SimpleMarket(
    "Bybit (Derivatives)",
    "https://api.bybit.com/derivatives/v3/public/instruments-info?limit=1000",
    "https://api.bybit.com/derivatives/v3/public/tickers?symbol=%1\$s",
    errorPropertyName = "retMsg"
) {
    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("result")
            .getJSONArray("list")
            .forEachJSONObject { market ->
                if(market.optString("status") == "Trading") {
                    val quoteCurrency: String?
                    val contractType: FuturesContractType

                    when(market.getString("contractType")){
                        "LinearPerpetual" -> {
                            quoteCurrency = market.getString("settleCoin")
                            contractType = FuturesContractType.PERPETUAL
                        }
                        "InversePerpetual" -> {
                            quoteCurrency = market.getString("quoteCoin")
                            contractType = FuturesContractType.INVERSE_PERPETUAL
                        }
                        else -> {
                            quoteCurrency = null
                            contractType = FuturesContractType.NONE
                        }
                    }

                    if(quoteCurrency != null) {
                        pairs.add(
                            CurrencyPairInfo(
                                currencyBase = market.getString("baseCoin"),
                                currencyCounter = quoteCurrency,
                                currencyPairId = market.getString("symbol"),
                                contractType = contractType
                            )
                        )
                    }
                }
            }
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {

        ticker.timestamp = jsonObject.getLong("time")

        jsonObject
            .getJSONObject("result")
            .getJSONArray("list")
            .getJSONObject(0)
            .apply {
                ticker.ask = getDouble("askPrice")
                ticker.bid = getDouble("bidPrice")

                ticker.high = getDouble("highPrice24h")
                ticker.low = getDouble("lowPrice24h")

                ticker.last = getDouble("lastPrice")

                val volume24h = getDouble("volume24h")
                val turnover24h  = getDouble("turnover24h")

                if(checkerInfo.contractType == FuturesContractType.INVERSE_PERPETUAL) {
                    ticker.vol = turnover24h
                    ticker.volQuote = volume24h
                } else {
                    ticker.vol = volume24h
                    ticker.volQuote = turnover24h
                }
            }
    }
}