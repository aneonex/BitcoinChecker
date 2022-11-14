package com.aneonex.bitcoinchecker.tester.data.local

import android.database.sqlite.SQLiteException
import androidx.annotation.WorkerThread
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.tester.domain.exceptions.DatabaseError
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import javax.inject.Inject

class MyMarketLocalDataSource @Inject constructor(private val appDao: MarketDao) {

    @WorkerThread
    suspend fun getMarketData(marketKey: String): MyMarketPairsInfo? {
        val marketData = appDao.getMarketWithPairs(marketKey) ?: return null

        return MyMarketPairsInfo(
            marketData.market.updateDate,
            marketData.pairs.map { CurrencyPairInfo(
                it.baseAsset,
                it.quoteAsset,
                it.marketPairId,
                it.contractType
            ) })
    }

    @WorkerThread
    suspend fun saveMarketData(marketKey: String, marketData: MyMarketPairsInfo) {
        try {
            appDao.saveMarketWithPairs(marketKey, marketData)
        } catch (ex: SQLiteException) {
            throw DatabaseError(ex)
        }
    }
}

