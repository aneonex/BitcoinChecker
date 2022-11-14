package com.aneonex.bitcoinchecker.tester.di

import com.aneonex.bitcoinchecker.tester.data.MyMarketRepository
import com.aneonex.bitcoinchecker.tester.data.MyMarketRepositoryImpl
import com.aneonex.bitcoinchecker.tester.data.local.MarketDao
import com.aneonex.bitcoinchecker.tester.data.local.MarketDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class MarketModule {

    @Binds
    abstract fun provideMyMarketRepository(
        myMarketRepositoryImp: MyMarketRepositoryImpl
    ): MyMarketRepository

    companion object {

        @Provides
        fun provideMarketDao(
            marketDatabase: MarketDatabase
        ): MarketDao = marketDatabase.getMarketDao()
    }
}
