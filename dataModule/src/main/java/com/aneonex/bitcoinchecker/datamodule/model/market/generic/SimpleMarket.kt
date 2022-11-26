package com.aneonex.bitcoinchecker.datamodule.model.market.generic

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import org.json.JSONObject

abstract class SimpleMarket(
        name: String,
        private val pairsUrl: String,
        private val tickerUrl: String,
        ttsName: String = name,
        private val errorPropertyName: String? = null
    ): Market(name, ttsName, null) {

    override fun getCurrencyPairsUrl(requestId: Int): String {
        return pairsUrl
    }

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return String.format(tickerUrl, getPairId(checkerInfo))
    }

    open fun getPairId(checkerInfo: CheckerInfo): String? {
        return checkerInfo.currencyPairId
    }

    override fun parseErrorFromJsonObject(
        requestId: Int,
        jsonObject: JSONObject,
        checkerInfo: CheckerInfo
    ): String? {
        errorPropertyName?.also {
            return@parseErrorFromJsonObject jsonObject.getString(it)
        }

        return super.parseErrorFromJsonObject(requestId, jsonObject, checkerInfo)
    }
}