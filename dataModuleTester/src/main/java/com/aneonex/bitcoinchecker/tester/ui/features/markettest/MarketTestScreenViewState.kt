package com.aneonex.bitcoinchecker.tester.ui.features.markettest

import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.tester.domain.model.MarketTickerResult
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarket
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import com.aneonex.bitcoinchecker.tester.ui.features.markettest.dto.MarketPairsUpdateState
import kotlinx.coroutines.flow.StateFlow

class MarketTestScreenViewState(
    val markets: List<MyMarket>,
    val currentMarket: StateFlow<MyMarket?>,
    val canUpdatePairs: StateFlow<Boolean>,

    val currentMarketPairsInfo: StateFlow<MyMarketPairsInfo?>,
    val marketPairsUpdateState: StateFlow<MarketPairsUpdateState>,

    val currentBaseAsset: StateFlow<String?>,
    val currentQuoteAsset: StateFlow<String?>,
    val currentContractType: StateFlow<FuturesContractType?>,

    val baseAssets: StateFlow<List<String>>,
    val quoteAssets: StateFlow<List<String>>,
    val contractTypes: StateFlow<List<FuturesContractType>>,

    val marketTicker: StateFlow<MarketTickerResult?>
)