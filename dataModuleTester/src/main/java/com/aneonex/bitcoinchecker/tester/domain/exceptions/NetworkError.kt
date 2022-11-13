package com.aneonex.bitcoinchecker.tester.domain.exceptions

import com.aneonex.bitcoinchecker.tester.domain.exceptions.MarketError

class NetworkError(cause: Throwable) : MarketError(cause)