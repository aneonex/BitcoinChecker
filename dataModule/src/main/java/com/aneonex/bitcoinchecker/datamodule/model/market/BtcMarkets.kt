package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import org.json.JSONObject

class BtcMarkets : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "BtcMarkets.net"
        private const val TTS_NAME = "BTC Markets net"
        //TODO Use https://api.btcmarkets.net/v3/markets to get list of active markets instead of this hardcoded list
        private const val URL = "https://api.btcmarkets.net/market/%1\$s/%2\$s/tick"
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.LTC] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETC] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ETH] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.XRP] = arrayOf(
                    VirtualCurrency.BTC,
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.BCH] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.COMP] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ALGO] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.MCAU] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.USDT] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.POWR] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.OMG] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.BSV] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.GNT] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.BAT] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.XLM] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.ENJ] = arrayOf(
                    Currency.AUD
            )
            CURRENCY_PAIRS[VirtualCurrency.LINK] = arrayOf(
                    Currency.AUD
            )
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.getDouble("bestBid")
        ticker.ask = jsonObject.getDouble("bestAsk")
        ticker.last = jsonObject.getDouble("lastPrice")
        ticker.timestamp = jsonObject.getLong("timestamp")
    }
}
