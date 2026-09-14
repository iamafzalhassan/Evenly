package org.example.evenly.data

import org.example.evenly.data.sources.ExpenseEntity
import org.example.evenly.data.sources.ExpenseShareEntity
import org.example.evenly.data.sources.ExpenseWithShares
import org.example.evenly.model.Currency
import org.example.evenly.model.Expense
import org.example.evenly.model.ExpenseId
import org.example.evenly.model.MemberId
import org.example.evenly.model.Money
import org.example.evenly.model.SplitRule
import kotlin.time.Instant

private const val SPLIT_KIND_EQUAL: String = "EQUAL"
private const val SPLIT_KIND_EXACT: String = "EXACT"
private const val SPLIT_KIND_PERCENTAGE: String = "PERCENTAGE"

internal fun Expense.toEntity(groupId: String): ExpenseEntity = ExpenseEntity(
    amountMinorUnits = amount.minorUnits,
    spentAtEpochMillis = spentAt.toEpochMilliseconds(),
    currencyCode = amount.currency.name,
    groupId = groupId,
    id = id.raw,
    paidByMemberId = paidBy.raw,
    splitKind = split.kind(),
    title = title,
)

internal fun Expense.toShareEntities(): List<ExpenseShareEntity> {
    val values = when (val rule = split) {
        is SplitRule.Equal -> rule.participants.map { it to 1L }
        is SplitRule.Exact -> rule.minorUnits.map { (memberId, minorUnits) -> memberId to minorUnits }
        is SplitRule.Percentage -> rule.basisPoints.map { (memberId, basisPoints) -> memberId to basisPoints.toLong() }
    }
    return values.mapIndexed { index, (memberId, value) -> ExpenseShareEntity(position = index, value = value, expenseId = id.raw, memberId = memberId.raw) }
}

internal fun ExpenseWithShares.toExpense(): Expense {
    val orderedShares = shares.sortedBy { it.position }
    val split = when (expense.splitKind) {
        SPLIT_KIND_EXACT -> SplitRule.Exact(minorUnits = orderedShares.associate { MemberId(it.memberId) to it.value })
        SPLIT_KIND_PERCENTAGE -> SplitRule.Percentage(basisPoints = orderedShares.associate { MemberId(it.memberId) to it.value.toInt() })
        else -> SplitRule.Equal(participants = orderedShares.map { MemberId(it.memberId) })
    }
    return Expense(
        title = expense.title,
        id = ExpenseId(expense.id),
        spentAt = Instant.fromEpochMilliseconds(expense.spentAtEpochMillis),
        paidBy = MemberId(expense.paidByMemberId),
        amount = Money(minorUnits = expense.amountMinorUnits, currency = Currency.fromCode(expense.currencyCode)),
        split = split,
    )
}

private fun SplitRule.kind(): String = when (this) {
    is SplitRule.Equal -> SPLIT_KIND_EQUAL
    is SplitRule.Exact -> SPLIT_KIND_EXACT
    is SplitRule.Percentage -> SPLIT_KIND_PERCENTAGE
}
