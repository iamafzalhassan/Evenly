package org.example.evenly.data.sources

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(childColumns = ["groupId"], entity = GroupEntity::class, onDelete = ForeignKey.CASCADE, parentColumns = ["id"])],
    indices = [Index(value = ["groupId"])],
    tableName = "settlements",
)
data class SettlementEntity(
    val amountMinorUnits: Long,
    val settledAtEpochMillis: Long,
    val currencyCode: String,
    val fromMemberId: String,
    val groupId: String,
    @PrimaryKey val id: String,
    val toMemberId: String,
)
