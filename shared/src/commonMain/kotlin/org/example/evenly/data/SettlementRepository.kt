package org.example.evenly.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.evenly.data.sources.SettlementDao
import org.example.evenly.model.GroupId
import org.example.evenly.model.Settlement
import org.example.evenly.model.SettlementId
import org.example.evenly.model.Transfer
import kotlin.time.Clock
import kotlin.uuid.Uuid

class SettlementRepository internal constructor(private val settlementDao: SettlementDao) {
    suspend fun deleteSettlement(id: SettlementId) = settlementDao.deleteSettlement(id.raw)

    fun observeSettlements(groupId: GroupId): Flow<List<Settlement>> = settlementDao.observeSettlements(groupId.raw).map { settlements -> settlements.map { it.toSettlement() } }

    suspend fun recordTransfer(groupId: GroupId, transfer: Transfer) {
        require(transfer.amount.isPositive) { "A payment must be a positive amount" }
        val settlement = Settlement(settledAt = Clock.System.now(), from = transfer.from, to = transfer.to, amount = transfer.amount, id = SettlementId(Uuid.random().toString()))
        settlementDao.insertSettlement(settlement.toEntity(groupId.raw))
    }
}
