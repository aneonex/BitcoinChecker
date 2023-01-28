package com.aneonex.bitcoinchecker.tester.data

import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.tester.domain.model.MarketTickerResult
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarket
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo

interface MyMarketRepository {
    suspend fun getMarketList(): List<MyMarket>
    suspend fun getMarketCurrencyPairsInfo(market: MyMarket): MyMarketPairsInfo
    fun isMarketSupportsUpdatePairs(market: MyMarket): Boolean

    suspend fun getMarketTicker(market: MyMarket, pairInfo: CurrencyPairInfo): MarketTickerResult
    suspend fun updateMarketCurrencyPairs(market: MyMarket)
}
