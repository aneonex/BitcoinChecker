package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import com.aneonex.bitcoinchecker.datamodule.model.currency.VirtualCurrency
import com.aneonex.bitcoinchecker.datamodule.util.TimeUtils
import org.json.JSONObject

class Mercado : Market(NAME, TTS_NAME, getCurrencyPairs()) {
    companion object {
        private const val NAME = "Mercado Bitcoin"
        private const val TTS_NAME = "Mercado"
        private const val URL = "https://www.mercadobitcoin.net/api/%1\$s/ticker/"

        @Suppress("SpellCheckingInspection")
        private fun getCurrencyPairs(): CurrencyPairsMap {
            // API Doc: https://www.mercadobitcoin.com.br/api-doc/
            val baseCurrencies = arrayOf(
                "AAVE",
                "ABFY",
                "ACH",
                "ADA",
                "ADS",
                "AGIX",
                "ALGO",
                "ALLFT",
                "ALPHA",
                "AMP",
                "ANT",
                "APE",
                "ATOM",
                "AUDIO",
                "AVAX",
                "AXS",

                "ASRFT",
                "ATMFT",

                "BAL",
                "BAND",
                "BAT",

                VirtualCurrency.BCH,
                VirtualCurrency.BTC,
                "CAIFT",
                "CHZ",
                "COMP",
                "COTI",
                "CRV",
                "CVC",
                "CVX",
                "DAI",
                "DIA",
                "DOGE",
                "DOT",

                VirtualCurrency.ETH,
                "FIL",
                "GALA",
                "GALFT",
                "ICP",
                "ILV",
                "JUVFT",
                "KEEP",
                VirtualCurrency.LINK,
                VirtualCurrency.LTC,
                "MANA",
                "MATIC",
                "MKR",
                "OCEAN",
                "OGN",
                "OMG",

                "PAXG",
                "PSGFT",
                "SOL",
                "SPELL",
                "STORJ",
                "STX",
                "SUSHI",
                "SYN",
                "UNI",
                VirtualCurrency.USDC,
                "USDP",
                "WBTC",
                "WBX",
                "WLUNA",
                "XLM",
                VirtualCurrency.XRP,
                "XTZ",
                "YFY",
                "ZRX",
            )

            val quoteCurrencies = arrayOf(Currency.BRL)
            return baseCurrencies.associateTo(CurrencyPairsMap()) { it to quoteCurrencies }
        }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        ticker: Ticker,
        checkerInfo: CheckerInfo
    ) {
        jsonObject.getJSONObject("ticker").also {
            ticker.bid = it.getDouble("buy")
            ticker.ask = it.getDouble("sell")
            ticker.vol = it.getDouble("vol")
            ticker.high = it.getDouble("high")
            ticker.low = it.getDouble("low")
            ticker.last = it.getDouble("last")
            ticker.timestamp = it.getLong("date") * TimeUtils.MILLIS_IN_SECOND
        }
    }
}