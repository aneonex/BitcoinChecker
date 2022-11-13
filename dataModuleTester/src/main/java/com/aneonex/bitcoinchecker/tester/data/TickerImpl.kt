package com.aneonex.bitcoinchecker.tester.data

import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.model.Ticker.Companion.NO_DATA

internal class TickerImpl: Ticker {
    override var bid: Double = NO_DATA_DOUBLE
    override var ask: Double = NO_DATA_DOUBLE
    override var vol: Double = NO_DATA_DOUBLE
    override var volQuote: Double = NO_DATA_DOUBLE
    override var high: Double = NO_DATA_DOUBLE
    override var low: Double = NO_DATA_DOUBLE
    override var last: Double = NO_DATA_DOUBLE
    override var timestamp: Long = NO_DATA.toLong()

    companion object {
        private const val NO_DATA_DOUBLE: Double = NO_DATA.toDouble()
    }
}