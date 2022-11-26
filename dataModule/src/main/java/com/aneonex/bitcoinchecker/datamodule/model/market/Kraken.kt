package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachName
import org.json.JSONObject

// Ref: https://docs.kraken.com/rest/#tag/Market-Data
class Kraken : SimpleMarket(
    "Kraken",
    "https://api.kraken.com/0/public/AssetPairs",
    "https://api.kraken.com/0/public/Ticker?pair=%1\$s"
) {

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONObject("result")
            .forEachName { pairId, pairJsonObject ->
                if (pairId.indexOf('.') == -1) {
                    pairs.add(
                        CurrencyPairInfo(
                            parseCurrency(pairJsonObject.getString("base")),
                            parseCurrency(pairJsonObject.getString("quote")),
                            pairId
                        )
                    )
                }
            }
    }

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return super.getPairId(checkerInfo) ?: (fixCurrency(checkerInfo.currencyBase) + fixCurrency(checkerInfo.currencyCounter))
    }

    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        val resultObject = jsonObject.getJSONObject("result")

        resultObject
            .getJSONObject(resultObject.names()!!.getString(0))
            .also {
                ticker.bid = getDoubleFromJsonArrayObject(it, "b")
                ticker.ask = getDoubleFromJsonArrayObject(it, "a")

                ticker.high = getDoubleFromJsonArrayObject(it, "h")
                ticker.low = getDoubleFromJsonArrayObject(it, "l")

                ticker.vol = getDoubleFromJsonArrayObject(it, "v")
                ticker.last = getDoubleFromJsonArrayObject(it, "c")
            }
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String? {
        return jsonObject
            .getJSONArray("error")
            .getString(0)
    }

    companion object {
        private fun fixCurrency(currency: String): String {
            if (VirtualCurrency.BTC == currency) return VirtualCurrency.XBT
            if (VirtualCurrency.VEN == currency) return VirtualCurrency.XVN
            return if (VirtualCurrency.DOGE == currency) VirtualCurrency.XDG else currency
        }

        private fun getDoubleFromJsonArrayObject(jsonObject: JSONObject, arrayKey: String): Double {
            return jsonObject
                .getJSONArray(arrayKey)
                .let {
                    if (it.length() > 0) it.getDouble(0) else 0.0
                }
        }

        private fun parseCurrency(currency: String): String {
            var resultCurrency = currency

            if (currency.length >= 2) {
                val firstChar = currency[0]
                if (firstChar == 'Z' || firstChar == 'X') {
                    resultCurrency = currency.substring(1)
                }
            }

            if (VirtualCurrency.XBT == resultCurrency) return VirtualCurrency.BTC
            if (VirtualCurrency.XVN == resultCurrency) return VirtualCurrency.VEN
            if (VirtualCurrency.XDG == resultCurrency) return VirtualCurrency.DOGE

            return resultCurrency
        }
    }
}