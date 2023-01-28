package com.aneonex.bitcoinchecker.tester.ui.features.markettest

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aneonex.bitcoinchecker.datamodule.model.CheckerInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.tester.R
import com.aneonex.bitcoinchecker.tester.data.TickerImpl
import com.aneonex.bitcoinchecker.tester.domain.model.MarketTickerResult
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarket
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import com.aneonex.bitcoinchecker.tester.ui.components.ComboBox
import com.aneonex.bitcoinchecker.tester.ui.components.LogBox
import com.aneonex.bitcoinchecker.tester.ui.components.Ticker
import com.aneonex.bitcoinchecker.tester.ui.features.error.ErrorScreen
import com.aneonex.bitcoinchecker.tester.ui.features.error.ErrorScreenViewState
import com.aneonex.bitcoinchecker.tester.ui.features.loading.LoadingScreen
import com.aneonex.bitcoinchecker.tester.ui.features.markettest.dto.MarketPairsUpdateState
import com.aneonex.bitcoinchecker.tester.ui.features.syncpairs.SyncPairsDialog
import com.aneonex.bitcoinchecker.tester.ui.theme.MyAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

@Composable
fun MarketTestScreen(marketViewModel: MarketTestViewModel = hiltViewModel()) {

    val uiState by marketViewModel.uiState.collectAsState()

    when (uiState) {
        is MarketTestUiState.Success -> {

            val viewState = (uiState as MarketTestUiState.Success).data

            MarketScreenMain(
                viewState = viewState,
                httpLogText = marketViewModel.httpLogText,

                onMarketChanged = marketViewModel::setCurrentMarket,

                onBaseAssetChanged = marketViewModel::setCurrentBaseAsset,
                onQuoteAssetChanged = marketViewModel::setCurrentQuoteAsset,
                onContractTypeChanged = marketViewModel::setCurrentContractType,

                onTestMarketButtonClick = marketViewModel::getTicker,
                onSyncCurrencyPairsClick = marketViewModel::syncCurrencyPairs,
                onSyncCurrencyPairsDialogClosed = marketViewModel::clearCurrencyPairsUpdateError
            )
        }

        is MarketTestUiState.Error -> {
            ErrorScreen(errorScreenViewState = ErrorScreenViewState((uiState as MarketTestUiState.Error).exception)) {
                marketViewModel.retryLoadMarketList()
            }
        }
        is MarketTestUiState.Loading -> {
            LoadingScreen()
        }
    }
}

@Composable
private fun MarketScreenMain(
    viewState: MarketTestScreenViewState,
    httpLogText: StateFlow<String>,

    onMarketChanged: (MyMarket?) -> Unit,

    onBaseAssetChanged: (String?) -> Unit,
    onQuoteAssetChanged: (String?) -> Unit,
    onContractTypeChanged: (FuturesContractType?) -> Unit,

    onTestMarketButtonClick: (MyMarket) -> Unit,
    onSyncCurrencyPairsClick: () -> Unit,
    onSyncCurrencyPairsDialogClosed: () -> Unit,
) {
    val basePadding = 8.dp

    val logText by httpLogText.collectAsState()

    val markets = viewState.markets
    val marketKeys = markets.map { market -> market.key }.also {
        Timber.d("XXX", "***** 2) REMAP MARKETS ******")
    }
    Timber.d("XXX", "***** 1) INIT MarketScreenMain ******")

    val currentMarket by viewState.currentMarket.collectAsState()
    val currentMarketIndex = markets.indexOf(currentMarket).also {
        Timber.d("XXX", "***** 3) REMAP MARKET INDEX ******")
    }

    val canUpdatePairs by viewState.canUpdatePairs.collectAsState()
    val currentMarketPairsInfo by viewState.currentMarketPairsInfo.collectAsState()

    val currentBaseAsset by viewState.currentBaseAsset.collectAsState()
    val currentQuoteAsset by viewState.currentQuoteAsset.collectAsState()
    val currentContractType by viewState.currentContractType.collectAsState()

    val baseAssets by viewState.baseAssets.collectAsState()
    val quoteAssets by viewState.quoteAssets.collectAsState()
    val contactTypes by viewState.contractTypes.collectAsState()

    val marketTicker by viewState.marketTicker.collectAsState()
    val marketPairsUpdateState by viewState.marketPairsUpdateState.collectAsState()

    var showSyncPairsDialog by remember { mutableStateOf(false) }

    if(showSyncPairsDialog){
        currentMarketPairsInfo?.also {
            SyncPairsDialog(
                it,
                marketPairsUpdateState,
                onSyncClick = onSyncCurrencyPairsClick,
                onDismiss = {
                    showSyncPairsDialog = false
                    onSyncCurrencyPairsDialogClosed()
                }
            )
        }
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier
            .padding(basePadding)
            .verticalScroll(scrollState)) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                Row(Modifier.padding(bottom = basePadding)) {
                    ComboBox(
                        modifier = Modifier
                            .weight(1f),
                        itemList = marketKeys,
                        selectedIndex = currentMarketIndex,
                        label = stringResource(id = R.string.market_screen_market),
                        onValueChange = { marketIndex ->
                            onMarketChanged(markets[marketIndex])
                        })
                }

                Row (
                    modifier = Modifier.padding(bottom = basePadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if(baseAssets.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .weight(4f),
                            text = stringResource(id = R.string.checker_add_check_currency_empty_warning_title),
                            textAlign = TextAlign.Center
                        )
                    } else {

                        ComboBox(
                            modifier = Modifier
                                .weight(2f),
                            itemList = baseAssets,
                            selectedIndex = baseAssets.indexOf(currentBaseAsset),
                            label = stringResource(id = R.string.market_screen_base),
                            onValueChange = { index ->
                                onBaseAssetChanged(baseAssets[index])
                            }
                        )

                        Text(
                            text = "/",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(
                                start = basePadding,
                                end = basePadding
                            )
                        )

                        ComboBox(
                            modifier = Modifier.weight(2f),
                            itemList = quoteAssets,
                            selectedIndex = quoteAssets.indexOf(currentQuoteAsset),
                            label = stringResource(id = R.string.market_screen_quote),
                            onValueChange = { index ->
                                onQuoteAssetChanged(quoteAssets[index])
                            }
                        )
                    }
                    Spacer(modifier = Modifier.size(basePadding))

                    Button(
                        onClick = {
                            showSyncPairsDialog = true
                        },
                        enabled = canUpdatePairs
                    ) {
                        Text(text = stringResource(id = R.string.market_screen_sync))
                    }
                }

                val hasContractTypes = contactTypes.isNotEmpty() && !(contactTypes.size == 1 && contactTypes[0] == FuturesContractType.NONE)

                if(hasContractTypes) {
                    Row(
                        modifier = Modifier.padding(bottom = basePadding),
                    ) {
                        ComboBox(
                            itemList = contactTypes.map { getContractTypeName(it) },
                            selectedIndex = contactTypes.indexOf(currentContractType),
                            label = stringResource(id = R.string.market_screen_contract_type),
                            onValueChange = { index ->
                                onContractTypeChanged(contactTypes[index])
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(bottom = basePadding),
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            currentMarket?.also {
                                onTestMarketButtonClick(it)
                            }
                        }) {
                        Text(
                            text = stringResource(id = R.string.market_screen_get_price),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                marketTicker
                    .also { marketTickerValue ->
                        if (marketTickerValue == null) {
                            Text(text = stringResource(id = R.string.generic_none))
                        } else {
                            if (marketTickerValue.error != null) {
                                Text(text = "Error: ${marketTickerValue.error}")
                            } else {
                                with(marketTickerValue.ticker) {
                                    Ticker(
                                        timestamp = timestamp,
                                        last = last,

                                        high = high,
                                        low = low,

                                        ask = ask,
                                        bid = bid,

                                        volBase = vol,
                                        volQuote = volQuote,

                                        currencyBase = marketTickerValue.pairInfo.currencyBase,
                                        currencyQuote = marketTickerValue.pairInfo.currencyCounter,
                                    )
                                }
                            }
                        }
                    }

                Spacer(modifier = Modifier.size(basePadding * 2))
                LogBox(logText)
            }
        }
    }
}

@Composable
private fun getContractTypeName(contractType: FuturesContractType): String {
    if(contractType == FuturesContractType.NONE)
        return stringResource(id = R.string.market_screen_spot)

    return contractType.toString()
}

/*
@Preview(showBackground = true)
@Composable
private fun MarketScreenPreview() {
    MarketTestScreen()
}
*/

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MarketScreenMainPreview() {
    val ticker = TickerImpl()
    ticker.last = 123.46

    val checkerInfo = CheckerInfo("BTC", "USD", null, FuturesContractType.NONE)

    MyAppTheme {
        MarketScreenMain(
            MarketTestScreenViewState(
                listOf(),
                MutableStateFlow<MyMarket?>(null),
                MutableStateFlow(true),

                MutableStateFlow<MyMarketPairsInfo?>(null),
                MutableStateFlow(MarketPairsUpdateState()),

                MutableStateFlow("BTC"),
                MutableStateFlow("USD"),
                MutableStateFlow(FuturesContractType.PERPETUAL),

                MutableStateFlow(listOf("BTC", "ETH")),
                MutableStateFlow(listOf("EUR", "USD")),
                MutableStateFlow(
                    listOf(
                        FuturesContractType.MONTHLY,
                        FuturesContractType.PERPETUAL
                    )
                ),

                MutableStateFlow(MarketTickerResult(ticker, checkerInfo))
            ),
            MutableStateFlow("My test message\nNew line 123"),

            onMarketChanged = {},
            onBaseAssetChanged = {},
            onQuoteAssetChanged = {},
            onContractTypeChanged = {},
            onTestMarketButtonClick = {},
            onSyncCurrencyPairsClick = {},
            onSyncCurrencyPairsDialogClosed = {}
        )
    }
}
