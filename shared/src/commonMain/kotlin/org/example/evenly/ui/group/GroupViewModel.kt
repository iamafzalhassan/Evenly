package org.example.evenly.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.evenly.data.ExpenseRepository
import org.example.evenly.data.GroupRepository
import org.example.evenly.data.SettlementRepository
import org.example.evenly.domain.BalanceCalculator
import org.example.evenly.domain.SettleUpPlanner
import org.example.evenly.model.Expense
import org.example.evenly.model.Group
import org.example.evenly.model.GroupId
import org.example.evenly.model.Settlement
import org.example.evenly.ui.STATE_STOP_TIMEOUT_MILLIS

class GroupViewModel(expenseRepository: ExpenseRepository, private val groupId: GroupId, private val groupRepository: GroupRepository, private val settlementRepository: SettlementRepository) : ViewModel() {
    private val actionFailed: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: StateFlow<GroupUiState> = combine(groupRepository.observeGroup(groupId), expenseRepository.observeExpenses(groupId), settlementRepository.observeSettlements(groupId), actionFailed) { group, expenses, settlements, isActionFailed ->
        toUiState(expenses = expenses, group = group, isActionFailed = isActionFailed, settlements = settlements)
    }.stateIn(initialValue = GroupUiState(), scope = viewModelScope, started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MILLIS))

    fun onEvent(event: GroupEvent) {
        when (event) {
            is GroupEvent.AddMember -> addMember(event.name)
            is GroupEvent.DeleteSettlement -> runAction { settlementRepository.deleteSettlement(event.settlementId) }
            GroupEvent.DismissActionFailure -> actionFailed.value = false
            is GroupEvent.RecordTransfer -> runAction { settlementRepository.recordTransfer(groupId = groupId, transfer = event.transfer) }
        }
    }

    private fun toUiState(isActionFailed: Boolean, expenses: List<Expense>, settlements: List<Settlement>, group: Group?): GroupUiState {
        if (group == null) return GroupUiState(isActionFailed = isActionFailed, isLoading = false)
        val balances = BalanceCalculator.balances(expenses = expenses, group = group, settlements = settlements)
        return GroupUiState(isActionFailed = isActionFailed, isLoading = false, balances = balances, expenses = expenses, settlements = settlements, transfers = SettleUpPlanner.plan(balances), group = group)
    }

    private fun addMember(name: String) {
        if (!state.value.isMemberNameAvailable(name)) return
        runAction { groupRepository.addMember(groupId = groupId, name = name) }
    }

    private fun runAction(write: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                write()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                actionFailed.value = true
            }
        }
    }
}
