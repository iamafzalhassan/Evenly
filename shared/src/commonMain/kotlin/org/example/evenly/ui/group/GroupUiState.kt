package org.example.evenly.ui.group

import androidx.compose.runtime.Immutable
import org.example.evenly.domain.ExpenseValuation
import org.example.evenly.domain.MemberUsage
import org.example.evenly.model.Balance
import org.example.evenly.model.Expense
import org.example.evenly.model.Group
import org.example.evenly.model.MemberId
import org.example.evenly.model.Money
import org.example.evenly.model.Settlement
import org.example.evenly.model.Transfer
import org.example.evenly.model.total

@Immutable
data class GroupUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val expenseQuery: String = "",
    val balances: List<Balance> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val group: Group? = null,
    val feedback: GroupFeedback? = null,
) {
    val hasActivity: Boolean get() = expenses.isNotEmpty() || settlements.isNotEmpty()

    val visibleExpenses: List<Expense>
        get() {
            val query = expenseQuery.trim()
            if (query.isEmpty()) return expenses
            val names = memberNames
            return expenses.filter { expense -> expense.title.contains(query, ignoreCase = true) || names[expense.paidBy].orEmpty().contains(query, ignoreCase = true) }
        }

    val memberNames: Map<MemberId, String> get() = group?.members.orEmpty().associate { it.id to it.name }

    val totalPaidBack: Money? get() = group?.let { current -> settlements.map { it.amount }.total(current.currency) }
    val totalSpent: Money? get() = group?.let { current -> expenses.map(ExpenseValuation::groupAmount).total(current.currency) }

    fun isMemberNameAvailable(name: String): Boolean {
        val trimmed = name.trim()
        val members = group?.members ?: return false
        return trimmed.isNotEmpty() && members.none { it.name.equals(trimmed, ignoreCase = true) }
    }

    fun memberRemoval(memberId: MemberId): MemberRemoval = when {
        MemberUsage.isReferenced(expenses = expenses, memberId = memberId, settlements = settlements) -> MemberRemoval.IN_USE
        group?.members.orEmpty().size <= Group.MIN_MEMBERS -> MemberRemoval.TOO_FEW_MEMBERS
        else -> MemberRemoval.ALLOWED
    }
}
