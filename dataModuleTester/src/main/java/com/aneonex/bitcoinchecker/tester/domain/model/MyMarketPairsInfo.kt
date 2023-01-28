package com.aneonex.bitcoinchecker.tester.domain.model

import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType

data class MyMarketPairsInfo(
    val lastSyncDate: Long = 0,
    val pairs: List<CurrencyPairInfo> = emptyList(),
) {
    val size: Int get() = pairs.size

    val baseCurrencies: Iterable<String> get() =
        pairs
            .map { it.currencyBase }
            .distinct()

    fun getQuoteCurrencies(baseCurrency: String): Iterable<String> =
        pairs
            .filter { it.currencyBase == baseCurrency }
            .map { it.currencyCounter }
            .distinct()

    fun getAvailableFuturesContractsTypes(baseCurrency: String?, quoteCurrency: String?): List<FuturesContractType> {
        if(baseCurrency == null || quoteCurrency == null) return emptyList()

        return pairs
            .filter {
                it.currencyBase == baseCurrency
                && it.currencyCounter == quoteCurrency }
            .map { it.contractType }
    }

    fun getCurrencyPairInfo(baseCurrency: String, quoteCurrency: String, contractType: FuturesContractType): CurrencyPairInfo? {
        // TODO: Optimize performance
        return pairs.firstOrNull {
            it.currencyBase == baseCurrency
                && it.currencyCounter == quoteCurrency
                && it.contractType == contractType
        }
    }
}