package com.aneonex.bitcoinchecker.tester.ui.features.markettest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aneonex.bitcoinchecker.datamodule.model.CurrencyPairInfo
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType
import com.aneonex.bitcoinchecker.tester.data.HttpLogger
import com.aneonex.bitcoinchecker.tester.data.MyMarketRepository
import com.aneonex.bitcoinchecker.tester.domain.exceptions.MarketError
import com.aneonex.bitcoinchecker.tester.domain.model.MarketTickerResult
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarket
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import com.aneonex.bitcoinchecker.tester.ui.features.markettest.dto.MarketPairsUpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MarketTestViewModel @Inject constructor(
    private val myMarketRepository: MyMarketRepository,
    private val httpLogger: HttpLogger
) : ViewModel() {
    private val _marketTicker = MutableStateFlow<MarketTickerResult?>(null)

    private val _currentMarket = MutableStateFlow<MyMarket?>(null)

    private val _marketPairsUpdateTrigger = MutableSharedFlow<Unit>(1)
    private val _marketPairsUpdateState = MutableStateFlow(MarketPairsUpdateState())

    private val _currentBaseAsset = MutableStateFlow<String?>(null)
    private val _currentQuoteAsset = MutableStateFlow<String?>(null)
    private val _currentContractType = MutableStateFlow<FuturesContractType?>(null)

    private val _reloadMarketsTrigger = MutableSharedFlow<Unit>(1)

    private var _currentMarketPairsInfo: StateFlow<MyMarketPairsInfo?> = _currentMarket
        .combine(_marketPairsUpdateTrigger){ market, _ -> market }
        .map { market ->
            if (market == null) null
            else myMarketRepository.getMarketCurrencyPairsInfo(market)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var _currentMarketCanUpdatePairs: StateFlow<Boolean> = _currentMarket
        .map { market ->
            if (market == null) false
            else myMarketRepository.isMarketSupportsUpdatePairs(market)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var _syncPairsJob: Job? = null

    private var _baseAssets: StateFlow<List<String>> = _currentMarketPairsInfo
        .map { pairsInfo -> pairsInfo?.baseCurrencies?.toList() ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var _quoteAssets: StateFlow<List<String>> = _currentMarketPairsInfo
        .combine(_currentBaseAsset) { pairsInfo, baseAsset ->
            if (baseAsset == null || pairsInfo == null)
                emptyList()
            else
                pairsInfo.getQuoteCurrencies(baseAsset).toList()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var _contractTypes: StateFlow<List<FuturesContractType>> =
        combine(
            _currentMarketPairsInfo,
            _currentBaseAsset,
            _currentQuoteAsset,
        )
         { pairsInfo, baseCurrency, quoteCurrency ->
            if (baseCurrency == null || quoteCurrency == null || pairsInfo == null)
                emptyList()
            else
                pairsInfo.getAvailableFuturesContractsTypes(baseCurrency, quoteCurrency)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var _currentMarketPair: StateFlow<CurrencyPairInfo?> =
        combine(
            _currentMarketPairsInfo,
            _currentBaseAsset,
            _currentQuoteAsset,
            _currentContractType
        ) {
            pairs, baseCurrency, quoteCurrency, futuresContractType ->
            if(pairs != null && baseCurrency != null && quoteCurrency != null )
                pairs.getCurrencyPairInfo(baseCurrency, quoteCurrency, futuresContractType ?: FuturesContractType.NONE)
            else
                null
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            _marketPairsUpdateTrigger.emit(Unit)
            _reloadMarketsTrigger.emit(Unit)

            launch {
                _baseAssets.collectLatest { newBaseAssets ->
                    if( _currentBaseAsset.value == null || !newBaseAssets.contains(_currentBaseAsset.value)) {
                        _currentBaseAsset.value = newBaseAssets.firstOrNull()
                    }
                }
            }

            launch {
                _quoteAssets.collectLatest { newQuoteAssets ->
                    if( _currentQuoteAsset.value == null || !newQuoteAssets.contains(_currentQuoteAsset.value)) {
                        _currentQuoteAsset.value = newQuoteAssets.firstOrNull()
                    }
                }
            }

            launch {
                _contractTypes.collectLatest { contractTypes ->
                    if( _currentContractType.value == null || !contractTypes.contains(_currentContractType.value)) {
                        _currentContractType.value = contractTypes.firstOrNull()
                    }
                }
            }

            launch {
                _currentMarketPair.collectLatest {
                    _marketTicker.value = null
                }
            }

            // Handle HTTP log messages
            launch {
                val lineMaxLength = 400
                val messageCountLimit = 10

                val messageList = mutableListOf<String>()
                httpLogger.messageFlow.collect {
                    if(messageList.size == messageCountLimit){
                        messageList.removeAt(0)
                    }

                    fun formatLogLine(line: String): String{
                        if(line.length <= lineMaxLength)
                            return line
                        return line.take(lineMaxLength) + "..."
                    }

                    messageList.add(formatLogLine(it))
                    _httpLogText.value = messageList.joinToString("\n")
                }
            }
        }
    }

    private val _httpLogText = MutableStateFlow("")
    val httpLogText = _httpLogText.asStateFlow()

    val uiState: StateFlow<MarketTestUiState> = _reloadMarketsTrigger
        .map {
            val marketList =
                try {
                    myMarketRepository.getMarketList().sortedBy { x -> x.name.lowercase() }
                } catch (e: Exception) {
                    return@map MarketTestUiState.Error(e)
                }

            _currentMarket.value.also {
                if(it == null || !marketList.contains(it)) {
                    _currentMarket.value = marketList.firstOrNull()
                }
            }

            MarketTestUiState.Success(
                MarketTestScreenViewState(
                    markets = marketList,
                    currentMarket = _currentMarket.asStateFlow(),
                    canUpdatePairs = _currentMarketCanUpdatePairs,

                    currentMarketPairsInfo = _currentMarketPairsInfo,
                    marketPairsUpdateState = _marketPairsUpdateState.asStateFlow(),

                    currentBaseAsset = _currentBaseAsset.asStateFlow(),
                    currentQuoteAsset = _currentQuoteAsset.asStateFlow(),
                    currentContractType = _currentContractType.asStateFlow(),

                    baseAssets = _baseAssets,
                    quoteAssets = _quoteAssets,
                    contractTypes = _contractTypes,

                    marketTicker = _marketTicker.asStateFlow()
                )
            )
        }
//        .catch { e ->
//            emit(UiState.Error(e))
//        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            //started = SharingStarted.Eagerly,
            initialValue = MarketTestUiState.Loading
        )

    fun getTicker(market: MyMarket) {
        _currentMarketPair.value?.also {
            viewModelScope.launch {
                try {
                    _marketTicker.value = myMarketRepository.getMarketTicker(market, it)
                } catch (e: Exception) {
                    _marketTicker.value = null
                }
            }
        }
    }

    fun setCurrentMarket(market: MyMarket?) {
        _currentMarket.value = market
    }

    fun setCurrentBaseAsset(asset: String?) {
        _currentBaseAsset.value = asset
    }

    fun setCurrentQuoteAsset(asset: String?) {
        _currentQuoteAsset.value = asset
    }

    fun setCurrentContractType(contractType: FuturesContractType?) {
        _currentContractType.value = contractType
    }

    @Synchronized
    fun syncCurrencyPairs() {
        _syncPairsJob?.cancel()
        _syncPairsJob = null

        _currentMarket.value?.also { market ->
            _marketPairsUpdateState.value = MarketPairsUpdateState(true)
            _syncPairsJob = viewModelScope.launch {
                Timber.d("Start market sync: ${market.key}")
                try {
                    myMarketRepository.updateMarketCurrencyPairs(market)
                    _marketPairsUpdateTrigger.emit(Unit)
                    _marketPairsUpdateState.value = MarketPairsUpdateState()
                } catch (ex: MarketError) {
                    _marketPairsUpdateState.value = MarketPairsUpdateState(error = ex.message)
                }
            }.also {
                it.invokeOnCompletion { exception: Throwable? ->
                     Timber.d(exception, "Market sync completed: %s", market.key)
                }
            }
        }
    }

    fun clearCurrencyPairsUpdateError() {
        _syncPairsJob?.cancel()
        _syncPairsJob = null

        _marketPairsUpdateState.value = MarketPairsUpdateState(false)
    }

    fun retryLoadMarketList() {
        viewModelScope.launch {
            _reloadMarketsTrigger.emit(Unit)
        }
    }
}

sealed interface MarketTestUiState {
    object Loading : MarketTestUiState

    data class Success(val data: MarketTestScreenViewState) : MarketTestUiState

    data class Error(val exception: Throwable) : MarketTestUiState
}