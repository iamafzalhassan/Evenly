package org.example.evenly.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExpenseDao {
    @Query("DELETE FROM expenses WHERE id = :id")
    abstract suspend fun deleteExpense(id: String)

    @Query("DELETE FROM expense_shares WHERE expenseId = :expenseId")
    abstract suspend fun deleteShares(expenseId: String)

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id")
    abstract suspend fun findExpense(id: String): ExpenseWithShares?

    @Insert
    abstract suspend fun insertShares(shares: List<ExpenseShareEntity>)

    @Transaction
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY spentAtEpochMillis DESC")
    abstract fun observeExpenses(groupId: String): Flow<List<ExpenseWithShares>>

    @Upsert
    abstract suspend fun upsertExpense(expense: ExpenseEntity)

    @Transaction
    open suspend fun saveExpenseWithShares(expense: ExpenseEntity, shares: List<ExpenseShareEntity>) {
        upsertExpense(expense)
        deleteShares(expense.id)
        insertShares(shares)
    }
}
