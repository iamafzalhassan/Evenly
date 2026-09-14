package org.example.evenly.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettlementDao {
    @Query("DELETE FROM settlements WHERE id = :id")
    abstract suspend fun deleteSettlement(id: String)

    @Insert
    abstract suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY settledAtEpochMillis DESC")
    abstract fun observeSettlements(groupId: String): Flow<List<SettlementEntity>>
}
