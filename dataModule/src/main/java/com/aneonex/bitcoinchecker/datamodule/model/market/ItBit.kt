package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject

class ItBit : SimpleMarket(
    "itBit (by Paxos)",
    "https://api.paxos.com/v2/markets",
    "https://api.paxos.com/v2/markets/%1\$s/ticker",
    "It Bit"
) {
    private fun fixCurrency(currency: String?): String? {
        return if (VirtualCurrency.BTC == currency) VirtualCurrency.XBT else currency
    }

    override fun getPairId(checkerInfo: CheckerInfo): String {
        return checkerInfo.currencyPairId ?: "${fixCurrency(checkerInfo.currencyBase)}{checkerInfo.currencyCounter}"
    }

    override fun parseCurrencyPairsFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        pairs: MutableList<CurrencyPairInfo>
    ) {
        jsonObject
            .getJSONArray("markets")
            .forEachJSONObject {
                pairs.add(CurrencyPairInfo(
                    it.getString("base_asset"),
                    it.getString("quote_asset"),
                    it.getString("market")
                ))
            }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {

        ticker.bid = jsonObject.getJSONObject("best_bid").getDouble("price")
        ticker.ask = jsonObject.getJSONObject("best_ask").getDouble("price")

        jsonObject.getJSONObject("last_day").also {
            ticker.vol = it.getDouble("volume")
            ticker.high = it.getDouble("high")
            ticker.low = it.getDouble("low")
        }

        ticker.last = jsonObject.getJSONObject("last_execution").getDouble("price")
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String? {
        return jsonObject
            .getJSONObject("error")
            .getString("message")
    }
}