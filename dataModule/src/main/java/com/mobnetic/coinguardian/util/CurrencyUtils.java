package com.mobnetic.coinguardian.util

import com.mobnetic.coinguardian.model.CurrencySubunit
import com.mobnetic.coinguardian.model.currency.CurrenciesSubunits
import com.mobnetic.coinguardian.model.currency.CurrencySymbols

object CurrencyUtils {
    fun getCurrencySymbol(currency: String?): String? {
        return if (CurrencySymbols.CURRENCY_SYMBOLS.containsKey(currency)) CurrencySymbols.CURRENCY_SYMBOLS[currency] else currency
    }

    fun getCurrencySubunit(currency: String?, subunitToUnit: Long): CurrencySubunit? {
        if (CurrenciesSubunits.CURRENCIES_SUBUNITS.containsKey(currency)) {
            val subunits = CurrenciesSubunits.CURRENCIES_SUBUNITS[currency]
            if (subunits!!.containsKey(subunitToUnit)) return subunits[subunitToUnit]
        }
        return CurrencySubunit(currency, 1)
    }
}