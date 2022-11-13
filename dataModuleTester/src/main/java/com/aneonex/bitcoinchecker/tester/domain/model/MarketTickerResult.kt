package com.aneonex.bitcoinchecker.tester.domain.model

import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.Ticker

class MarketTickerResult(
    val ticker: Ticker,
    val pairInfo: CheckerInfo,
    val error: String? = null
)