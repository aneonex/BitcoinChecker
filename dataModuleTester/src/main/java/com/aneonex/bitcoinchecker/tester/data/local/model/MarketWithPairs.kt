package com.aneonex.bitcoinchecker.tester.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class MarketWithPairs (
    @Embedded val market: MarketEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "marketId",
        entity = MarketPairEntity::class
    )
    val pairs: List<MarketPairEntity>
)