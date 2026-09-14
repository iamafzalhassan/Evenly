package org.example.evenly.domain

import org.example.evenly.model.Expense
import org.example.evenly.model.MemberId
import org.example.evenly.model.Settlement
import org.example.evenly.model.SplitRule

object MemberUsage {
    fun isReferenced(expenses: List<Expense>, settlements: List<Settlement>, memberId: MemberId): Boolean {
        val inExpense = expenses.any { expense -> expense.paidBy == memberId || memberId in expense.split.memberIds() }
        val inSettlement = settlements.any { settlement -> settlement.from == memberId || settlement.to == memberId }
        return inExpense || inSettlement
    }

    private fun SplitRule.memberIds(): Set<MemberId> = when (this) {
        is SplitRule.Equal -> participants.toSet()
        is SplitRule.Exact -> minorUnits.keys
        is SplitRule.Percentage -> basisPoints.keys
    }
}
