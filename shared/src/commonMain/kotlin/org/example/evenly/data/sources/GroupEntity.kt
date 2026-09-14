package org.example.evenly.data.sources

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_groups")
data class GroupEntity(val createdAtEpochMillis: Long, val currencyCode: String, @PrimaryKey val id: String, val name: String)
