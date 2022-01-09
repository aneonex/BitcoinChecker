package com.aneonex.bitcoinchecker.datamodule.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.aneonex.bitcoinchecker.datamodule.model.CurrencySubunit
import java.text.DecimalFormat
import java.util.*

object FormatUtilsBase {

    // Call when application changes locale
    fun updateLocale() {
        // Recreate formatter
        decimalFormatStore = DecimalFormatStore()
    }

    // ========================================
    // Double formatting (using default locale)
    // ========================================
    private class DecimalFormatStore {
        val formatNoDecimal = DecimalFormat("#,###")
        val formatTwoDecimal = DecimalFormat("#,###.00")
        val formatFourSignificantAtMost = DecimalFormat("@###")
        val formatEightSignificantAtMost = DecimalFormat("@#######")
    }

    private var decimalFormatStore = DecimalFormatStore()

    // ====================
    // Format methods
    // ====================
    fun formatDouble(value: Double/*, isPrice: Boolean*/): String {
        val decimalFormat: DecimalFormat = when {
            value < 10 -> decimalFormatStore.formatFourSignificantAtMost
            value < 10000 -> decimalFormatStore.formatTwoDecimal
            else -> decimalFormatStore.formatNoDecimal
        }

        return formatDouble(decimalFormat, value)
    }

    @Suppress("unused")
    fun formatDoubleWithEightMax(value: Double): String {
        return formatDouble(decimalFormatStore.formatEightSignificantAtMost, value)
    }

    @Suppress("unused")
    fun formatDoubleWithFourMax(value: Double): String {
        return formatDouble(decimalFormatStore.formatFourSignificantAtMost, value)
    }

    private fun formatDouble(decimalFormat: DecimalFormat, value: Double): String {
        synchronized(decimalFormat) {
            try {
                return decimalFormat.format(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return value.toString()
        }
    }

    // ====================
    // Price formatting
    // ====================
    fun formatPriceWithCurrency(price: Double, subunitDst: CurrencySubunit): String {
        return formatPriceWithCurrency(price, subunitDst, true)
    }

    fun formatPriceWithCurrency(price: Double, subunitDst: CurrencySubunit, showCurrencyDst: Boolean): String {
        var priceString = formatPriceValueForSubunit(price, subunitDst, forceInteger = false, skipNoSignificantDecimal = false)
        if (showCurrencyDst) {
            priceString = formatPriceWithCurrency(priceString, subunitDst.name)
        }
        return priceString
    }

    fun formatPriceWithCurrency(value: Double, currency: String): String {
        return formatPriceWithCurrency(formatDouble(value), currency)
    }

    private fun formatPriceWithCurrency(priceString: String, currency: String): String {
        return priceString + " " + CurrencyUtils.getCurrencySymbol(currency)
    }

    fun formatPriceValueForSubunit(price: Double, subunitDst: CurrencySubunit, forceInteger: Boolean, skipNoSignificantDecimal: Boolean): String {
        val calcPrice = price * subunitDst.subunitToUnit.toDouble()
        return if (!subunitDst.allowDecimal || forceInteger) return (calcPrice + 0.5f).toLong().toString()
            else if (skipNoSignificantDecimal) formatDoubleWithEightMax(calcPrice) else formatDouble(calcPrice)
    }

    // ====================
    // Date && Time formatting
    // ====================
    @kotlin.jvm.JvmStatic
    fun formatSameDayTimeOrDate(context: Context?, time: Long): String {
        return if (DateUtils.isToday(time)) {
            DateFormat.getTimeFormat(context).format(Date(time))
        } else {
            DateFormat.getDateFormat(context).format(Date(time))
        }
    }

    fun formatDateTime(context: Context?, time: Long): String {
        val date = Date(time)
        return DateFormat.getTimeFormat(context).format(date) + ", " + DateFormat.getDateFormat(context).format(date)
    }
}