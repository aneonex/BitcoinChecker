package com.aneonex.bitcoinchecker.datamodule.model

open class CurrencyPairInfo(val currencyBase: String, val currencyCounter: String, val currencyPairId: String?) : Comparable<CurrencyPairInfo> {
    @Throws(NullPointerException::class)
    override fun compareTo(another: CurrencyPairInfo): Int {
        if (currencyBase == null || another.currencyBase == null || currencyCounter == null || another.currencyCounter == null) throw NullPointerException()
        val compBase = currencyBase.compareTo(another.currencyBase, ignoreCase = true)
        return if (compBase != 0) compBase else currencyCounter.compareTo(another.currencyCounter, ignoreCase = true)
    }
}