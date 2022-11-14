package com.aneonex.bitcoinchecker.tester.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "markets",
    indices = [Index("marketKey")]
)
data class MarketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val marketKey: String,

    @ColumnInfo
    val updateDate: Long
)