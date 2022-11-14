package com.aneonex.bitcoinchecker.tester.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aneonex.bitcoinchecker.datamodule.model.Ticker
import com.aneonex.bitcoinchecker.datamodule.util.FormatUtilsBase
import com.aneonex.bitcoinchecker.tester.R

@Composable
fun Ticker(
    timestamp: Long,
    last: Double,

    high: Double,
    low: Double,

    ask: Double,
    bid: Double,

    volBase: Double,
    volQuote: Double,

    currencyBase: String,
    currencyQuote: String,
) {
    val spaceSize = 6.dp

    Column {
        TitleValueItem(
            R.string.ticker_timestamp,
            FormatUtilsBase.formatSameDayTimeOrDate(LocalContext.current, timestamp))

        PriceItem(R.string.ticker_last, last, currencyQuote)

        if(high > Ticker.NO_DATA) {
            Spacer(modifier = Modifier.size(spaceSize))
            PriceItem(R.string.ticker_high, high, currencyQuote)
            PriceItem(R.string.ticker_low, low, currencyQuote)
        }

        if(ask > Ticker.NO_DATA) {
            Spacer(modifier = Modifier.size(spaceSize))
            PriceItem(R.string.ticker_ask, ask, currencyQuote)
            PriceItem(R.string.ticker_bid, bid, currencyQuote)
        }

        Spacer(modifier = Modifier.size(spaceSize))
        if(volBase > Ticker.NO_DATA)
            PriceItem(R.string.ticker_vol_base, volBase, currencyBase)
        if(volQuote > Ticker.NO_DATA)
            PriceItem(R.string.ticker_vol_quote, volQuote, currencyQuote)
    }
}

@Composable
private fun PriceItem(@StringRes titleId: Int, price: Double, currency: String) {
    TitleValueItem(titleId = titleId, value = FormatUtilsBase.formatPriceWithCurrency(price, currency))
}

@Composable
private fun TitleValueItem(@StringRes titleId: Int, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
//            style = Typography.bodyMedium,
            text = stringResource(id = titleId) + ":"
        )
        Text(
            text = value,
//            style = Typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TickerPreview(){
    Ticker(
        timestamp = 1668282179000,
        last = 18521.18,

        high = 20011.55,
        low =  16544.2,

        ask = 18622.3456,
        bid = 14633.0,

        volBase = 100_000.0,
        volQuote = 200_000_000.555,

        currencyBase = "BTC",
        currencyQuote = "USDT"
    )
}