package com.aneonex.bitcoinchecker.tester.data.remote

import com.aneonex.bitcoinchecker.datamodule.model.*
import com.aneonex.bitcoinchecker.tester.data.TickerImpl
import com.aneonex.bitcoinchecker.tester.data.remote.util.await
import com.aneonex.bitcoinchecker.tester.domain.exceptions.*
import com.aneonex.bitcoinchecker.tester.domain.model.MyMarketPairsInfo
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.io.InvalidObjectException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max

class MyMarketRemoteDataSource @Inject constructor(
    private val httpClient: OkHttpClient
) {

    suspend fun fetchMarketCurrencyPairsInfo(market: Market): MyMarketPairsInfo {
        val pairs: MutableList<CurrencyPairInfo> = ArrayList()
        val numOfRequests = market.currencyPairsNumOfRequests

        val nextPairs: MutableList<CurrencyPairInfo> = ArrayList()
        for (requestId in 0 until numOfRequests) {
            try {
                val nextUrl = market.getCurrencyPairsUrl(requestId)
                val nextPostRequestBody = market.getCurrencyPairsPostRequestInfo(requestId)

                if (!nextUrl.isNullOrEmpty()) {
/*
                    val responseString = MarketHttp.httpClient.callMarket(nextUrl, nextPostRequestBody) {
                        timeout {
                            requestTimeoutMillis = 120_000
                            socketTimeoutMillis = 120_000
                        }
                    }
*/
                    val responseString = httpClient
                        .newBuilder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
                        .callMarket(nextUrl, nextPostRequestBody)

                    nextPairs.clear()
                    try {
                        market.parseCurrencyPairsMain(requestId, responseString, nextPairs)
                    } catch (ex: Exception) {
                        ex.rethrowIfCritical()
                        if (requestId == 0) {
                            throw ParseError(ex)
                        }
                    }
                    pairs.addAll(nextPairs)
                }
            } catch (ex: Exception) {
                ex.rethrowIfCritical()
                if (requestId == 0) {
                    throw ex
                }
            }
        }

        pairs.sort()

        return MyMarketPairsInfo(
            lastSyncDate = System.currentTimeMillis(),
            pairs = pairs
        )
    }

    suspend fun fetchMarketTicker(market: Market, checkerInfo: CheckerInfo): Ticker {

        val ticker = TickerImpl()

        updateMarketTicker(
            ticker,
            httpClient,
            market,
            0,
            checkerInfo
        )

        val numOfRequests = market.getNumOfRequests(checkerInfo)
        if (numOfRequests > 1) {
            // Executing additional requests
            for (requestId in 1 until numOfRequests) {
                try {
                    updateMarketTicker(
                        ticker,
                        httpClient,
                        market,
                        requestId,
                        checkerInfo
                    )

                } catch (ex: Exception) {
                    ex.rethrowIfCritical()
                    // e.printStackTrace()
                    Timber.e(ex, "Failed to execute additional request #$requestId")
                }
            }
        }

        return ticker // Success
    }
}

suspend fun OkHttpClient.callMarket(url: String, postRequestInfo: PostRequestInfo?): String {
    // Prevent to hang OkHttp request
    return withTimeout(max(callTimeoutMillis, 15_000) * 2 + 5_000L) {
        callMarketInternal(url, postRequestInfo)
    }
}

private suspend fun OkHttpClient.callMarketInternal(url: String, postRequestInfo: PostRequestInfo?): String {
    fun getResponseString(response: Response): String {
        val responseString = response.body.string()

        if(!response.isSuccessful)
            throw HttpMarketError(response.code, responseString)

        return responseString
    }

    try {
        val requestBuilder = Request.Builder().url(url)

        if (postRequestInfo == null) {
            // logger.debug { "Market GET request: $url" }
            // HTTP GET
            val request = requestBuilder.build()
            return getResponseString(this.newCall(request).await())
        }

        // HTTP POST
        //logger.debug { "Market POST request: $url" }

        postRequestInfo.headers?.let {
            it.forEach { (name, value) ->
                requestBuilder.addHeader(name, value)
            }
        }

        val request = requestBuilder
            .post(postRequestInfo.body.toRequestBody())
            .build()
        return  getResponseString(this.newCall(request).await())

    } catch (ex: Exception) {
        ex.rethrowIfCritical()
        throw parseMarketError(ex)
    }
}

private suspend fun updateMarketTicker(ticker: Ticker, httpClient: OkHttpClient, market: Market, requestId: Int, checkerInfo: CheckerInfo) {
    val url = market.getUrl(requestId, checkerInfo)

    if(url.isEmpty()) {
        if(requestId > 0)
            return

        throw IllegalArgumentException("Url is empty (market=${market.key})")
    }

    val postRequestInfo = market.getPostRequestInfo(requestId, checkerInfo)
    val responseString = httpClient.callMarket(url, postRequestInfo)

    if (responseString.isEmpty())
        throw UserFriendlyMarketError("Response data is empty") // TODO: Move to res

    try {
        market.parseTickerMain(requestId, responseString, ticker, checkerInfo)

        if(ticker.last <= Ticker.NO_DATA)
            throw InvalidObjectException("Parsed ticker has no data")
    } catch (ex: MarketError){
        throw ex
    } catch (ex: Exception){
        ex.rethrowIfCritical()

        val errorMessage: String?

        // Try to parse error message from response
        try {
            errorMessage = market.parseErrorMain(
                    requestId,
                    responseString,
                    checkerInfo
                )
        } catch (ex2: Exception) {
            ex2.rethrowIfCritical()

            // Failed to parse, re-throw original exception
            throw ex
        }

        throw UserFriendlyMarketError(errorMessage ?: "Unknown error (empty)")
    }
}
