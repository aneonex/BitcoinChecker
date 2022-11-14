package com.aneonex.bitcoinchecker.tester.domain.exceptions

import android.os.DeadSystemException
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.CancellationException

open class MarketError : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

// Rethrow coroutine cancellation exception
fun Exception.rethrowIfCritical() {
//    if(this.javaClass == TimeoutCancellationException::class.java)
//        return

    if(this is CancellationException // For OkHttp
        || this is DeadSystemException // The system is going to reboot or shutdown
    ) {
        throw this
    }
/*
    // For ktor
    if(this.javaClass == CancellationException::class.java)
        throw this
 */
}

fun parseMarketError(ex: Throwable): MarketError {
    return when(ex) {
        is MarketError ->
            ex

//            is HttpRequestTimeoutException,
//            is SocketTimeoutException,
//            is ConnectTimeoutException,
        //is TimeoutCancellationException,
        is InterruptedIOException ->
            TimeoutError(ex)

        is IOException -> // SocketException, UnknownHostException
            NetworkError(ex)

        else -> UnknownMarketError(ex)
    }
}
