package com.aneonex.bitcoinchecker.tester.data

import android.content.res.Resources
import com.aneonex.bitcoinchecker.datamodule.config.MarketsConfig
import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.Market
import com.aneonex.bitcoinchecker.datamodule.model.currency.CurrencyPairsMap
import com.aneonex.bitcoinchecker.tester.data.local.MyMarketLocalDataSource
import com.aneonex.bitcoinchecker.tester.data.remote.MyMarketRemoteDataSource
import com.aneonex.bitcoinchecker.tester.domain.exceptions.rethrowIfCritical
import com.aneonex.bitcoinchecker.tester.domain.model.MarketTickerResult
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarket
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import kotlinx.coroutines.delay
import javax.inject.Inject

class MyMarketRepositoryImpl @Inject constructor(
//    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
    private val marketLocalDataSource: MyMarketLocalDataSource,
    private val marketRemoteDataSource: MyMarketRemoteDataSource
    ) : MyMarketRepository {

    override suspend fun getMarketList(): List<MyMarket> {
        delay(3000) // Emulate loading

        return MarketsConfig.MARKETS.values.toList()
            .map{ it.mapToMyMarket() }
    }

    override fun isMarketSupportsUpdatePairs(market: MyMarket): Boolean =
        !getSourceMarketByKey(market).getCurrencyPairsUrl(0).isNullOrEmpty()


    override suspend fun getMarketCurrencyPairsInfo(market: MyMarket): MyMarketPairsInfo {
        return marketLocalDataSource.getMarketData(market.key) ?:
            MyMarketPairsInfo(
                lastSyncDate = 0,
                pairs = getSourceMarketByKey(market).currencyPairs?.let { convertPairsMapToPairList(it) } ?: emptyList()
            )
    }

    override suspend fun getMarketTicker(
        market: MyMarket,
        pairInfo: CurrencyPairInfo
    ): MarketTickerResult {

        val checkerInfo = pairInfo.toCheckerInfo()

        return try {
            val ticker = marketRemoteDataSource.fetchMarketTicker(
                getSourceMarketByKey(market),
                checkerInfo
            )
            MarketTickerResult(ticker, checkerInfo)
        } catch (ex: Exception) {
            ex.rethrowIfCritical()

            MarketTickerResult(TickerImpl(), checkerInfo, ex.message) // TODO: Parse exception
        }
    }

    override suspend fun updateMarketCurrencyPairs(market: MyMarket) {
        val marketData =
            marketRemoteDataSource.fetchMarketCurrencyPairsInfo(getSourceMarketByKey(market))

        if(marketData.size > 0) {
            marketLocalDataSource.saveMarketData(market.key, marketData)
        }
    }
}

private fun CurrencyPairInfo.toCheckerInfo(): CheckerInfo =
    CheckerInfo(
        this.currencyBase,
        this.currencyCounter,
        this.currencyPairId,
        this.contractType
    )

private fun getSourceMarketByKey(market: MyMarket): Market =
    getSourceMarketByKey(market.key)

private fun getSourceMarketByKey(marketKey: String): Market =
    MarketsConfig.MARKETS[marketKey]
        ?: throw Resources.NotFoundException("Market not found (key=${marketKey})")

private fun Market.mapToMyMarket(): MyMarket =
    MyMarket(key = key, name = name)

private fun convertPairsMapToPairList(currencyMap: CurrencyPairsMap): List<CurrencyPairInfo> {
    return currencyMap.flatMap { item ->
        item.value.map { quoteAsset ->
            CurrencyPairInfo(item.key, quoteAsset, null)
        }
    }
}
