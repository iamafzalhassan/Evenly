package org.example.evenly.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.evenly.data.ExpenseRepository
import org.example.evenly.data.GroupRepository
import org.example.evenly.data.SettlementRepository
import org.example.evenly.data.sync.SyncCoordinator
import org.example.evenly.domain.BalanceCalculator
import org.example.evenly.domain.SettleUpPlanner
import org.example.evenly.model.Expense
import org.example.evenly.model.Group
import org.example.evenly.model.GroupId
import org.example.evenly.model.MemberId
import org.example.evenly.model.Settlement
import org.example.evenly.ui.STATE_STOP_TIMEOUT_MILLIS

class GroupViewModel(
    expenseRepository: ExpenseRepository,
    private val groupId: GroupId,
    private val groupRepository: GroupRepository,
    private val settlementRepository: SettlementRepository,
    private val syncCoordinator: SyncCoordinator,
) : ViewModel() {
    private val inputs: MutableStateFlow<GroupScreenInputs> = MutableStateFlow(GroupScreenInputs())

    val state: StateFlow<GroupUiState> = combine(groupRepository.observeGroup(groupId), expenseRepository.observeExpenses(groupId), settlementRepository.observeSettlements(groupId), inputs) { group, expenses, settlements, screenInputs ->
        toUiState(expenses = expenses, group = group, inputs = screenInputs, settlements = settlements)
    }.stateIn(initialValue = GroupUiState(), scope = viewModelScope, started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MILLIS))

    fun onEvent(event: GroupEvent) {
        when (event) {
            is GroupEvent.AddMember -> addMember(event.name)
            is GroupEvent.ChangeExpenseQuery -> inputs.update { current -> current.copy(expenseQuery = event.text) }
            GroupEvent.DeleteGroup -> runAction(onSuccess = { setFeedback(GroupFeedback.GROUP_DELETED) }) { groupRepository.deleteGroup(groupId) }
            is GroupEvent.DeleteSettlement -> runAction { settlementRepository.deleteSettlement(event.settlementId) }
            GroupEvent.DismissFeedback -> setFeedback(null)
            is GroupEvent.RecordTransfer -> runAction { settlementRepository.recordTransfer(groupId = groupId, transfer = event.transfer) }
            GroupEvent.Refresh -> refresh()
            is GroupEvent.RemoveMember -> removeMember(event.memberId)
            is GroupEvent.RenameGroup -> renameGroup(event.name)
        }
    }

    private fun toUiState(expenses: List<Expense>, settlements: List<Settlement>, group: Group?, inputs: GroupScreenInputs): GroupUiState {
        if (group == null) return GroupUiState(isLoading = false, isRefreshing = inputs.isRefreshing, expenseQuery = inputs.expenseQuery, feedback = inputs.feedback)
        val balances = BalanceCalculator.balances(expenses = expenses, group = group, settlements = settlements)
        return GroupUiState(
            isLoading = false,
            isRefreshing = inputs.isRefreshing,
            expenseQuery = inputs.expenseQuery,
            balances = balances,
            expenses = expenses,
            settlements = settlements,
            transfers = SettleUpPlanner.plan(balances),
            group = group,
            feedback = inputs.feedback,
        )
    }

    private fun addMember(name: String) {
        if (!state.value.isMemberNameAvailable(name)) return
        runAction { groupRepository.addMember(groupId = groupId, name = name) }
    }

    private fun refresh() {
        inputs.update { current -> current.copy(isRefreshing = true) }
        viewModelScope.launch {
            val isSynced = syncCoordinator.syncNow()
            inputs.update { current -> current.copy(isRefreshing = false, feedback = if (isSynced) current.feedback else GroupFeedback.SYNC_FAILED) }
        }
    }

    private fun removeMember(memberId: MemberId) {
        if (state.value.memberRemoval(memberId) != MemberRemoval.ALLOWED) return
        runAction { check(groupRepository.removeMember(memberId)) { "Member is still referenced" } }
    }

    private fun renameGroup(name: String) {
        if (name.isBlank()) return
        runAction { groupRepository.renameGroup(id = groupId, name = name) }
    }

    private fun runAction(onSuccess: () -> Unit = {}, write: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                write()
                onSuccess()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                setFeedback(GroupFeedback.ACTION_FAILED)
            }
        }
    }

    private fun setFeedback(feedback: GroupFeedback?) {
        inputs.update { current -> current.copy(feedback = feedback) }
    }
}
