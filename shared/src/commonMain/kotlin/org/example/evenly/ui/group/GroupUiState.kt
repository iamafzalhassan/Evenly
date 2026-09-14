package org.example.evenly.ui.group

import androidx.compose.runtime.Immutable
import org.example.evenly.model.Balance
import org.example.evenly.model.Expense
import org.example.evenly.model.Group
import org.example.evenly.model.MemberId
import org.example.evenly.model.Settlement
import org.example.evenly.model.Transfer

@Immutable
data class GroupUiState(
    val isActionFailed: Boolean = false,
    val isLoading: Boolean = true,
    val balances: List<Balance> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val group: Group? = null,
) {
    val hasActivity: Boolean get() = expenses.isNotEmpty() || settlements.isNotEmpty()

    val memberNames: Map<MemberId, String> get() = group?.members.orEmpty().associate { it.id to it.name }

    fun isMemberNameAvailable(name: String): Boolean {
        val trimmed = name.trim()
        val members = group?.members ?: return false
        return trimmed.isNotEmpty() && members.none { it.name.equals(trimmed, ignoreCase = true) }
    }
}
