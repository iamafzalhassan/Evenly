package org.example.evenly.data.sources

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(childColumns = ["groupId"], entity = GroupEntity::class, onDelete = ForeignKey.CASCADE, parentColumns = ["id"])],
    indices = [Index(value = ["groupId"])],
    tableName = "members",
)
data class MemberEntity(val position: Int, val groupId: String, @PrimaryKey val id: String, val name: String)
