package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.currency.Currency
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import org.json.JSONObject

class Bit2c : Market(NAME, TTS_NAME, getCurrencies()) {
    companion object {
        private const val NAME = "Bit2c"
        private const val TTS_NAME = "Bit 2c"
        private const val URL = "https://bit2c.co.il/Exchanges/%1\$s%2\$s/Ticker.json"

        //GET https://bit2c.co.il/Exchanges/[BtcNis/EthNis/BchNis/LtcNis/EtcNis/BtgNis]/Ticker.json
        private fun getCurrencies(): CurrencyPairsMap =
            arrayOf(
                "BTC",
                "ETH",
                "BCHABC",
                "LTC",
                "ETC",
                "BTG",
                "USDC",
                "BCHSV",
                "GRIN",
            ).let {
                it.associateWithTo(CurrencyPairsMap()) { arrayOf(Currency.NIS) }
            }
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(URL, checkerInfo.currencyBase, checkerInfo.currencyCounter)
    }

    @Throws(Exception::class)
    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        ticker.bid = jsonObject.optDouble("h", ticker.bid)
        ticker.ask = jsonObject.optDouble("l", ticker.ask)

        ticker.vol = jsonObject.getDouble("a")
        ticker.last = jsonObject.getDouble("ll")
    }
}