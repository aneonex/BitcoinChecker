package com.aneonex.bitcoinchecker.tester.data

import kotlinx.coroutines.flow.SharedFlow

interface HttpLogger {
    val messageFlow: SharedFlow<String>
}