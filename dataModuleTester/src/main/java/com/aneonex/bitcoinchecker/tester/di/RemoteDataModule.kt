package com.aneonex.bitcoinchecker.tester.di

import com.aneonex.bitcoinchecker.tester.data.HttpLogger
import com.aneonex.bitcoinchecker.tester.data.remote.HttpLoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(httpLogger: HttpLogger): HttpLoggingInterceptor =
        HttpLoggingInterceptor(httpLogger as HttpLoggingInterceptor.Logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
//            setLevel(HttpLoggingInterceptor.Level.BASIC)
        }

    @Singleton
    @Provides
    fun provideHttpLoggerFlow(): HttpLogger =
        HttpLoggerImpl()
}