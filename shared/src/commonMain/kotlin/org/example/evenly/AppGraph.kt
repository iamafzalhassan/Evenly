package org.example.evenly

import androidx.room.RoomDatabase
import org.example.evenly.data.ExpenseRepository
import org.example.evenly.data.GroupRepository
import org.example.evenly.data.SettlementRepository
import org.example.evenly.data.sources.EvenlyDatabase
import org.example.evenly.data.sources.buildEvenlyDatabase

class AppGraph internal constructor(databaseBuilder: RoomDatabase.Builder<EvenlyDatabase>) {
    private val database: EvenlyDatabase = databaseBuilder.buildEvenlyDatabase()

    val expenseRepository: ExpenseRepository = ExpenseRepository(database.expenseDao())

    val groupRepository: GroupRepository = GroupRepository(database.groupDao())

    val settlementRepository: SettlementRepository = SettlementRepository(database.settlementDao())
}
