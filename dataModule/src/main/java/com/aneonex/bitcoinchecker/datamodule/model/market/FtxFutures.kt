package com.aneonex.bitcoinchecker.datamodule.model.market

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.market.generic.SimpleMarket
import com.aneonex.bitcoinchecker.datamodule.util.forEachJSONObject
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FtxFutures : SimpleMarket(
    "FTX Futures",
    "https://ftx.com/api/futures",
    "https://ftx.com/api/futures/%1\$s",
    errorPropertyName = "error"
) {
    override fun getPairId(checkerInfo: CheckerInfo): String {
        return getPairId(checkerInfo.currencyPairId!!, checkerInfo.contractType, checkerInfo.currencyBase)
    }

    override fun parseCurrencyPairsFromJsonObject(requestId: Int, jsonObject: JSONObject, pairs: MutableList<CurrencyPairInfo>) {
        val quartDatePart = formatDeliveryDateSuffix(FuturesContractType.getDeliveryDate(FuturesContractType.QUARTERLY)!!)
        val biQuartDatePart = formatDeliveryDateSuffix(FuturesContractType.getDeliveryDate(FuturesContractType.BIQUARTERLY)!!)

        jsonObject.getJSONArray("result").forEachJSONObject {  market ->
            val pairId = market.getString("name")

            val contactType = when(market.getString("type")) {
                "perpetual" -> FuturesContractType.PERPETUAL
                "future" -> when(market.getString("group")) {
                    "quarterly" -> when {
                        pairId.endsWith(quartDatePart) -> FuturesContractType.QUARTERLY
                        pairId.endsWith(biQuartDatePart) -> FuturesContractType.BIQUARTERLY
                        else -> return@forEachJSONObject
                    }
                    else -> return@forEachJSONObject
                }
                else -> return@forEachJSONObject
            }

           val baseAsset = market.getString("underlying")
           val formattedPairId = when(contactType) {
               FuturesContractType.QUARTERLY -> "$baseAsset:Q1"
               FuturesContractType.BIQUARTERLY -> "$baseAsset:Q2"
               else -> pairId
           }

            pairs.add( CurrencyPairInfo(
                baseAsset,
                "USD",
                formattedPairId,
                contactType
            ))
        }
    }

    override fun parseTickerFromJsonObject(requestId: Int, jsonObject: JSONObject, ticker: Ticker, checkerInfo: CheckerInfo) {
        jsonObject.getJSONObject("result").also { market ->
            ticker.bid = market.getDouble("bid")
            ticker.ask = market.getDouble("ask")
            ticker.last = market.getDouble("last")

            if(ticker.last > 0) {
                ticker.volQuote = market.getDouble("volumeUsd24h")
                ticker.vol = ticker.volQuote / ticker.last // Calculated base volume
            }
        }
    }

    companion object {
        private val FUTURES_DATE_FORMAT = DateTimeFormatter.ofPattern("MMdd", Locale.ROOT)

        private fun formatDeliveryDateSuffix(date: LocalDate): String = FUTURES_DATE_FORMAT.format(date)

        private fun getPairId(pairId: String, contactType: FuturesContractType, baseAsset: String): String {
            return when(contactType) {
                FuturesContractType.QUARTERLY,
                FuturesContractType.BIQUARTERLY -> getPairId(baseAsset, FuturesContractType.getDeliveryDate(contactType)!!)
                else -> pairId
            }
        }

        private fun getPairId(baseAsset: String, deliveryDate: LocalDate): String =
            "${baseAsset.uppercase()}-${formatDeliveryDateSuffix(deliveryDate)}"
    }
}