package org.example.evenly.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
abstract class SyncDao {
    @Query("DELETE FROM expense_shares WHERE expenseId = :expenseId")
    abstract suspend fun deleteShares(expenseId: String)

    @Transaction
    @Query("SELECT * FROM expenses WHERE isDirty = 1")
    abstract suspend fun dirtyExpenses(): List<ExpenseWithShares>

    @Query("SELECT * FROM expense_groups WHERE isDirty = 1")
    abstract suspend fun dirtyGroups(): List<GroupEntity>

    @Query("SELECT * FROM members WHERE isDirty = 1")
    abstract suspend fun dirtyMembers(): List<MemberEntity>

    @Query("SELECT * FROM settlements WHERE isDirty = 1")
    abstract suspend fun dirtySettlements(): List<SettlementEntity>

    @Query("SELECT isDirty, modifiedAtEpochMillis FROM expenses WHERE id = :id")
    abstract suspend fun expenseState(id: String): RowSyncState?

    @Query("SELECT isDirty, modifiedAtEpochMillis FROM expense_groups WHERE id = :id")
    abstract suspend fun groupState(id: String): RowSyncState?

    @Insert
    abstract suspend fun insertShares(shares: List<ExpenseShareEntity>)

    @Query("UPDATE expenses SET isDirty = 0 WHERE id = :id AND modifiedAtEpochMillis = :modifiedAtEpochMillis")
    abstract suspend fun markExpenseClean(modifiedAtEpochMillis: Long, id: String)

    @Query("UPDATE expense_groups SET isDirty = 0 WHERE id = :id AND modifiedAtEpochMillis = :modifiedAtEpochMillis")
    abstract suspend fun markGroupClean(modifiedAtEpochMillis: Long, id: String)

    @Query("UPDATE members SET isDirty = 0 WHERE id = :id AND modifiedAtEpochMillis = :modifiedAtEpochMillis")
    abstract suspend fun markMemberClean(modifiedAtEpochMillis: Long, id: String)

    @Query("UPDATE settlements SET isDirty = 0 WHERE id = :id AND modifiedAtEpochMillis = :modifiedAtEpochMillis")
    abstract suspend fun markSettlementClean(modifiedAtEpochMillis: Long, id: String)

    @Query("SELECT isDirty, modifiedAtEpochMillis FROM members WHERE id = :id")
    abstract suspend fun memberState(id: String): RowSyncState?

    @Query("UPDATE expense_groups SET inviteCode = :inviteCode WHERE id = :id")
    abstract suspend fun saveInviteCode(id: String, inviteCode: String)

    @Query("SELECT isDirty, modifiedAtEpochMillis FROM settlements WHERE id = :id")
    abstract suspend fun settlementState(id: String): RowSyncState?

    @Upsert
    abstract suspend fun upsertExpense(expense: ExpenseEntity)

    @Upsert
    abstract suspend fun upsertGroup(group: GroupEntity)

    @Upsert
    abstract suspend fun upsertMember(member: MemberEntity)

    @Upsert
    abstract suspend fun upsertSettlement(settlement: SettlementEntity)

    @Transaction
    open suspend fun saveRemoteExpense(expense: ExpenseEntity, shares: List<ExpenseShareEntity>) {
        upsertExpense(expense)
        deleteShares(expense.id)
        insertShares(shares)
    }
}
