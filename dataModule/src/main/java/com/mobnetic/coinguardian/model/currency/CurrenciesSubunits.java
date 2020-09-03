package com.mobnetic.coinguardian.model.currency

import com.mobnetic.coinguardian.model.CurrencySubunit
import com.mobnetic.coinguardian.model.CurrencySubunitsMap
import java.util.*

object CurrenciesSubunits {
    val CURRENCIES_SUBUNITS = HashMap<String?, CurrencySubunitsMap?>()

    init {
        CURRENCIES_SUBUNITS[VirtualCurrency.BTC] = CurrencySubunitsMap(
                CurrencySubunit(VirtualCurrency.BTC, 1),
                CurrencySubunit(VirtualCurrency.mBTC, 1000),
                CurrencySubunit(VirtualCurrency.uBTC, 1000000),
                CurrencySubunit(VirtualCurrency.Satoshi, 100000000, false)
        )
        CURRENCIES_SUBUNITS[VirtualCurrency.LTC] = CurrencySubunitsMap(
                CurrencySubunit(VirtualCurrency.LTC, 1),
                CurrencySubunit(VirtualCurrency.mLTC, 1000)
        )
    }
}