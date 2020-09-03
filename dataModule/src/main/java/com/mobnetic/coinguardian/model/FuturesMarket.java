package com.mobnetic.coinguardian.model

import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap

abstract class FuturesMarket(name: String, ttsName: String, currencyPairs: CurrencyPairsMap?, contractTypes: IntArray)
    : Market(name, ttsName, currencyPairs) {

    val contractTypes = contractTypes

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return getUrl(requestId, checkerInfo, checkerInfo.contractType)
    }

    protected abstract fun getUrl(requestId: Int, checkerInfo: CheckerInfo, contractType: Int): String
}