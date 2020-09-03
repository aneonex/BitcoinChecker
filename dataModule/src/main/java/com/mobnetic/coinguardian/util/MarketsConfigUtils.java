package com.mobnetic.coinguardian.util

import com.mobnetic.coinguardian.config.MarketsConfig
import com.mobnetic.coinguardian.model.Market
import com.mobnetic.coinguardian.model.market.Unknown
import java.util.*

object MarketsConfigUtils {
    private val UNKNOWN: Market = Unknown()
    fun getMarketById(id: Int): Market? {
        synchronized(MarketsConfig.MARKETS) {
            if (id >= 0 && id < MarketsConfig.MARKETS.size) {
                return ArrayList(MarketsConfig.MARKETS.values)[id]
            }
        }
        return UNKNOWN
    }

    @kotlin.jvm.JvmStatic
    fun getMarketByKey(key: String?): Market? {
        synchronized(MarketsConfig.MARKETS) { if (MarketsConfig.MARKETS.containsKey(key)) return MarketsConfig.MARKETS[key] }
        return UNKNOWN
    }

    fun getMarketIdByKey(key: String): Int {
        var i = 0
        for (market in MarketsConfig.MARKETS.values) {
            if (market!!.key == key) return i
            ++i
        }
        return 0
    }
}