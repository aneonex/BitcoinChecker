package com.aneonex.bitcoinchecker.tester.data.remote

import com.aneonex.bitcoinchecker.tester.data.HttpLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.logging.HttpLoggingInterceptor

internal class HttpLoggerImpl: HttpLoggingInterceptor.Logger, HttpLogger {
    private val _messageFlow = MutableSharedFlow<String>(1)

    override val messageFlow: SharedFlow<String>
        get() = _messageFlow

    override fun log(message: String) {
        _messageFlow.tryEmit(message )
    }
}