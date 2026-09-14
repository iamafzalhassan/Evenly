package org.example.evenly.ui.expenseeditor

import androidx.compose.runtime.Immutable
import org.example.evenly.domain.ExpenseSplitter
import org.example.evenly.domain.SplitError
import org.example.evenly.model.ExpenseId
import org.example.evenly.model.Group
import org.example.evenly.model.Member
import org.example.evenly.model.MemberId
import org.example.evenly.model.Money
import org.example.evenly.model.SplitRule
import org.example.evenly.util.MoneyFormat
import org.example.evenly.util.PercentFormat
import kotlin.time.Instant

@Immutable
data class ExpenseEditorUiState(
    val isDeleteFailed: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val isSaveFailed: Boolean = false,
    val isSaving: Boolean = false,
    val amountText: String = "",
    val title: String = "",
    val exactAmountTexts: Map<MemberId, String> = emptyMap(),
    val percentageTexts: Map<MemberId, String> = emptyMap(),
    val participantIds: Set<MemberId> = emptySet(),
    val expenseId: ExpenseId? = null,
    val group: Group? = null,
    val spentAt: Instant? = null,
    val paidBy: MemberId? = null,
    val splitMode: SplitMode = SplitMode.EQUAL,
) {
    val canSave: Boolean get() = !isLoading && !isSaving && title.isNotBlank() && paidBy != null && amount != null && splitRule != null && splitError == null
    val isEditing: Boolean get() = expenseId != null

    val assignedBasisPoints: Int get() = members.sumOf { PercentFormat.parseBasisPoints(percentageTexts[it.id].orEmpty()) ?: 0 }

    val members: List<Member> get() = group?.members.orEmpty()

    val shares: Map<MemberId, Money>
        get() {
            val currentAmount = amount ?: return emptyMap()
            val rule = splitRule ?: return emptyMap()
            return if (ExpenseSplitter.validate(currentAmount, rule) == null) ExpenseSplitter.shares(currentAmount, rule) else emptyMap()
        }

    val paidByMember: Member? get() = members.firstOrNull { it.id == paidBy }

    val amount: Money?
        get() {
            val currency = group?.currency ?: return null
            val minorUnits = MoneyFormat.parseMinorUnits(amountText, currency) ?: return null
            return Money(minorUnits = minorUnits, currency = currency)
        }

    val assignedAmount: Money?
        get() {
            val currency = group?.currency ?: return null
            return Money(minorUnits = members.sumOf { MoneyFormat.parseMinorUnits(exactAmountTexts[it.id].orEmpty(), currency) ?: 0L }, currency = currency)
        }

    val splitError: SplitError?
        get() {
            val currentAmount = amount ?: return null
            val rule = splitRule ?: return null
            return ExpenseSplitter.validate(currentAmount, rule)
        }

    val splitRule: SplitRule?
        get() {
            val currency = group?.currency ?: return null
            return when (splitMode) {
                SplitMode.EQUAL -> SplitRule.Equal(participants = members.map { it.id }.filter { it in participantIds })
                SplitMode.EXACT -> SplitRule.Exact(minorUnits = members.associate { member -> member.id to (parseOrZero(exactAmountTexts[member.id]) { MoneyFormat.parseMinorUnits(it, currency) } ?: return null) })
                SplitMode.PERCENTAGE -> SplitRule.Percentage(basisPoints = members.associate { member -> member.id to (parseOrZero(percentageTexts[member.id]) { PercentFormat.parseBasisPoints(it)?.toLong() }?.toInt() ?: return null) })
            }
        }

    private fun parseOrZero(text: String?, parse: (String) -> Long?): Long? = if (text.isNullOrBlank()) 0L else parse(text)
}
