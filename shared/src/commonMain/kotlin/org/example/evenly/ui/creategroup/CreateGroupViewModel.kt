package org.example.evenly.ui.creategroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.evenly.data.GroupRepository
import org.example.evenly.model.GroupId

class CreateGroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {
    private val mutableState: MutableStateFlow<CreateGroupUiState> = MutableStateFlow(CreateGroupUiState())

    val state: StateFlow<CreateGroupUiState> = mutableState.asStateFlow()

    fun onEvent(event: CreateGroupEvent) {
        when (event) {
            CreateGroupEvent.AddMember -> mutableState.update { state -> state.copy(memberNames = state.memberNames + "") }
            is CreateGroupEvent.ChangeCurrency -> mutableState.update { state -> state.copy(currency = event.currency) }
            is CreateGroupEvent.ChangeMemberName -> mutableState.update { state -> state.copy(memberNames = state.memberNames.mapIndexed { index, name -> if (index == event.index) event.name else name }) }
            is CreateGroupEvent.ChangeName -> mutableState.update { state -> state.copy(name = event.name) }
            CreateGroupEvent.Create -> create()
            CreateGroupEvent.DismissSaveFailure -> mutableState.update { state -> state.copy(isSaveFailed = false) }
            is CreateGroupEvent.RemoveMember -> mutableState.update { state -> if (state.canRemoveMember) state.copy(memberNames = state.memberNames.filterIndexed { index, _ -> index != event.index }) else state }
        }
    }

    private fun create() {
        val draft = mutableState.value
        if (!draft.canCreate) return
        mutableState.update { state -> state.copy(isSaving = true) }
        viewModelScope.launch {
            val groupId = save(draft)
            mutableState.update { state -> if (groupId == null) state.copy(isSaveFailed = true, isSaving = false) else state.copy(isSaving = false, createdGroupId = groupId) }
        }
    }

    private suspend fun save(draft: CreateGroupUiState): GroupId? = try {
        groupRepository.createGroup(name = draft.name, memberNames = draft.trimmedMemberNames, currency = draft.currency)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        null
    }
}
