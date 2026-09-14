package org.example.evenly.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.evenly.data.sources.ExpenseDao
import org.example.evenly.domain.ExpenseSplitter
import org.example.evenly.model.Expense
import org.example.evenly.model.ExpenseId
import org.example.evenly.model.GroupId
import org.example.evenly.model.MemberId
import org.example.evenly.model.Money
import org.example.evenly.model.SplitRule
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ExpenseRepository internal constructor(private val expenseDao: ExpenseDao) {
    suspend fun deleteExpense(id: ExpenseId) = expenseDao.deleteExpense(id.raw)

    suspend fun findExpense(id: ExpenseId): Expense? = expenseDao.findExpense(id.raw)?.toExpense()

    fun observeExpenses(groupId: GroupId): Flow<List<Expense>> = expenseDao.observeExpenses(groupId.raw).map { expenses -> expenses.map { it.toExpense() } }

    suspend fun saveExpense(title: String, id: ExpenseId?, groupId: GroupId, spentAt: Instant?, paidBy: MemberId, amount: Money, split: SplitRule) {
        val error = ExpenseSplitter.validate(amount, split)
        require(error == null) { "Invalid split: $error" }
        val expense = Expense(
            title = title.trim(),
            id = id ?: ExpenseId(Uuid.random().toString()),
            spentAt = spentAt ?: Clock.System.now(),
            paidBy = paidBy,
            amount = amount,
            split = split,
        )
        expenseDao.saveExpenseWithShares(expense = expense.toEntity(groupId.raw), shares = expense.toShareEntities())
    }
}
