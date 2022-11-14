package com.aneonex.bitcoinchecker.tester.data.local.model

import androidx.room.*
import com.aneonex.bitcoinchecker.datamodule.model.FuturesContractType

@Entity(
    tableName = "market_pairs",
    foreignKeys = [
        ForeignKey(
            entity = MarketEntity::class,
            parentColumns = ["id"],
            childColumns = ["marketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["marketId", "baseAsset", "quoteAsset", "contractType"]
)
data class MarketPairEntity(
    @ColumnInfo
    val marketId: Long,

    @ColumnInfo
    val baseAsset: String,

    @ColumnInfo
    val quoteAsset: String,

    @ColumnInfo
    val contractType: FuturesContractType,

    @ColumnInfo
    val marketPairId: String?,
)