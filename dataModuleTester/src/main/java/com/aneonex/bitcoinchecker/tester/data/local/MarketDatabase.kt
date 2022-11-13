package com.aneonex.bitcoinchecker.tester.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aneonex.bitcoinchecker.tester.data.local.MarketDatabase.Companion.VERSION
import com.aneonex.bitcoinchecker.tester.data.local.model.MarketEntity
import com.aneonex.bitcoinchecker.tester.data.local.model.MarketPairEntity

@Database(
    entities = [
        MarketEntity::class,
        MarketPairEntity::class,
    ],
    version = VERSION,
    exportSchema = true,
)

abstract class MarketDatabase : RoomDatabase() {
    abstract fun getMarketDao(): MarketDao

    companion object {
        const val VERSION = 1
        const val DB_NAME = "MarketDatabase.db"
    }
}
