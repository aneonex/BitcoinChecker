package com.aneonex.bitcoinchecker.datamodule.util

import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairsListWithDate
import java.util.*

class CurrencyPairsMapHelper(currencyPairsListWithDate: CurrencyPairsListWithDate?) {
    val date: Long
    val currencyPairs: HashMap<String?, Array<CharSequence?>>
    private val currencyPairsIds: HashMap<String, String?>
    var pairsCount = 0
    fun getCurrencyPairId(currencyBase: String?, currencyCounter: String?): String? {
        return currencyPairsIds[createCurrencyPairKey(currencyBase, currencyCounter)]
    }

    private fun createCurrencyPairKey(currencyBase: String?, currencyCounter: String?): String {
        return String.format("%1\$s_%2\$s", currencyBase, currencyCounter)
    }

    init {
        currencyPairs = LinkedHashMap()
        currencyPairsIds = HashMap()

        if (currencyPairsListWithDate == null) {
            date = 0
        }
        else {
            date = currencyPairsListWithDate.date
            val sortedPairs = currencyPairsListWithDate.pairs
            pairsCount = sortedPairs!!.size
            val currencyGroupSizes = HashMap<String?, Int>()
            for (currencyPairInfo in sortedPairs) {
                var currentCurrencyGroupSize = currencyGroupSizes[currencyPairInfo.currencyBase]
                if (currentCurrencyGroupSize == null) {
                    currentCurrencyGroupSize = 1
                } else {
                    ++currentCurrencyGroupSize
                }
                currencyGroupSizes[currencyPairInfo.currencyBase] = currentCurrencyGroupSize
            }
            var currentGroupPositionToInsert = 0
            for (currencyPairInfo in sortedPairs) {
                var currencyGroup = currencyPairs[currencyPairInfo.currencyBase]
                if (currencyGroup == null) {
                    currencyGroup = arrayOfNulls(currencyGroupSizes[currencyPairInfo.currencyBase]!!)
                    currencyPairs[currencyPairInfo.currencyBase] = currencyGroup
                    currentGroupPositionToInsert = 0
                } else {
                    ++currentGroupPositionToInsert
                }
                currencyGroup[currentGroupPositionToInsert] = currencyPairInfo.currencyCounter
                if (currencyPairInfo.currencyPairId != null) {
                    currencyPairsIds[createCurrencyPairKey(currencyPairInfo.currencyBase, currencyPairInfo.currencyCounter)] = currencyPairInfo.currencyPairId
                }
            }
        }
    }
}