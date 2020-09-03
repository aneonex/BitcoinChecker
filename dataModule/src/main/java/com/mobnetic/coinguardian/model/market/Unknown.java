package com.mobnetic.coinguardian.model.market

import com.mobnetic.coinguardian.model.CheckerInfo
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.currency.CurrencyPairsMap
import com.mobnetic.coinguardian.model.currency.VirtualCurrency
import com.mobnetic.coinguardiandatamodule.R
import java.util.*

class Unknown : Market(NAME, TTS_NAME, CURRENCY_PAIRS) {
    companion object {
        private const val NAME = "UNKNOWN"
        private const val TTS_NAME = NAME
        private const val URL = ""
        private val CURRENCY_PAIRS: CurrencyPairsMap = CurrencyPairsMap()

        init {
            CURRENCY_PAIRS[VirtualCurrency.BTC] = arrayOf(VirtualCurrency.BTC)
        }
    }

    override val cautionResId: Int
        get() = R.string.market_caution_unknown

    override fun getUrl(requestId: Int, checkerInfo: CheckerInfo): String {
        return URL
    }
}