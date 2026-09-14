package org.example.evenly.ui.expenseeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.evenly.data.ExpenseRepository
import org.example.evenly.data.GroupRepository
import org.example.evenly.model.Expense
import org.example.evenly.model.ExpenseId
import org.example.evenly.model.Group
import org.example.evenly.model.GroupId
import org.example.evenly.model.MemberId
import org.example.evenly.model.SplitRule
import org.example.evenly.util.MoneyFormat
import org.example.evenly.util.PercentFormat

class ExpenseEditorViewModel(expenseId: ExpenseId?, private val expenseRepository: ExpenseRepository, private val groupId: GroupId, groupRepository: GroupRepository) : ViewModel() {
    private val mutableState: MutableStateFlow<ExpenseEditorUiState> = MutableStateFlow(ExpenseEditorUiState())

    val state: StateFlow<ExpenseEditorUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val group = groupRepository.observeGroup(groupId).first()
            val expense = expenseId?.let { expenseRepository.findExpense(it) }
            mutableState.update { state -> if (expense == null) state.newDraft(group) else state.editDraft(expense = expense, group = group) }
        }
    }

    fun onEvent(event: ExpenseEditorEvent) {
        when (event) {
            is ExpenseEditorEvent.ChangeAmount -> mutableState.update { state -> state.copy(amountText = event.text) }
            is ExpenseEditorEvent.ChangeExactAmount -> mutableState.update { state -> state.copy(exactAmountTexts = state.exactAmountTexts + (event.memberId to event.text)) }
            is ExpenseEditorEvent.ChangePaidBy -> mutableState.update { state -> state.copy(paidBy = event.memberId) }
            is ExpenseEditorEvent.ChangePercentage -> mutableState.update { state -> state.copy(percentageTexts = state.percentageTexts + (event.memberId to event.text)) }
            is ExpenseEditorEvent.ChangeSplitMode -> mutableState.update { state -> state.copy(splitMode = event.mode) }
            is ExpenseEditorEvent.ChangeTitle -> mutableState.update { state -> state.copy(title = event.text) }
            ExpenseEditorEvent.Delete -> delete()
            ExpenseEditorEvent.DismissDeleteFailure -> mutableState.update { state -> state.copy(isDeleteFailed = false) }
            ExpenseEditorEvent.DismissSaveFailure -> mutableState.update { state -> state.copy(isSaveFailed = false) }
            ExpenseEditorEvent.Save -> save()
            is ExpenseEditorEvent.ToggleParticipant -> mutableState.update { state -> state.copy(participantIds = state.participantIds.toggled(event.memberId)) }
        }
    }

    private fun ExpenseEditorUiState.newDraft(group: Group?): ExpenseEditorUiState {
        val memberIds = group?.members.orEmpty().map { it.id }
        return copy(isLoading = false, participantIds = memberIds.toSet(), group = group, paidBy = memberIds.firstOrNull())
    }

    private fun ExpenseEditorUiState.editDraft(expense: Expense, group: Group?): ExpenseEditorUiState {
        val currency = expense.amount.currency
        val rule = expense.split
        return copy(
            isLoading = false,
            amountText = MoneyFormat.formatInput(currency = currency, minorUnits = expense.amount.minorUnits),
            title = expense.title,
            exactAmountTexts = if (rule is SplitRule.Exact) rule.minorUnits.mapValues { (_, minorUnits) -> MoneyFormat.formatInput(currency = currency, minorUnits = minorUnits) } else emptyMap(),
            percentageTexts = if (rule is SplitRule.Percentage) rule.basisPoints.mapValues { (_, basisPoints) -> PercentFormat.formatInput(basisPoints) } else emptyMap(),
            participantIds = if (rule is SplitRule.Equal) rule.participants.toSet() else group?.members.orEmpty().map { it.id }.toSet(),
            expenseId = expense.id,
            group = group,
            spentAt = expense.spentAt,
            paidBy = expense.paidBy,
            splitMode = when (rule) {
                is SplitRule.Equal -> SplitMode.EQUAL
                is SplitRule.Exact -> SplitMode.EXACT
                is SplitRule.Percentage -> SplitMode.PERCENTAGE
            },
        )
    }

    private fun delete() {
        val expenseId = mutableState.value.expenseId ?: return
        viewModelScope.launch {
            val isDeleted = attempt { expenseRepository.deleteExpense(expenseId) }
            mutableState.update { state -> if (isDeleted) state.copy(isFinished = true) else state.copy(isDeleteFailed = true) }
        }
    }

    private fun save() {
        val draft = mutableState.value
        val amount = draft.amount
        val paidBy = draft.paidBy
        val split = draft.splitRule
        if (!draft.canSave || amount == null || paidBy == null || split == null) return
        mutableState.update { state -> state.copy(isSaving = true) }
        viewModelScope.launch {
            val isStored = attempt { expenseRepository.saveExpense(amount = amount, groupId = groupId, id = draft.expenseId, paidBy = paidBy, spentAt = draft.spentAt, split = split, title = draft.title) }
            mutableState.update { state -> if (isStored) state.copy(isFinished = true, isSaving = false) else state.copy(isSaveFailed = true, isSaving = false) }
        }
    }

    private suspend fun attempt(write: suspend () -> Unit): Boolean = try {
        write()
        true
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        false
    }

    private fun Set<MemberId>.toggled(memberId: MemberId): Set<MemberId> = if (memberId in this) this - memberId else this + memberId
}
