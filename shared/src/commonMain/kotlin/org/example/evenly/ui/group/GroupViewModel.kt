package org.example.evenly.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.evenly.data.GroupRepository
import org.example.evenly.model.GroupId
import org.example.evenly.ui.STATE_STOP_TIMEOUT_MILLIS

class GroupViewModel(private val groupId: GroupId, private val groupRepository: GroupRepository) : ViewModel() {
    val state: StateFlow<GroupUiState> = groupRepository.observeGroup(groupId)
        .map { group -> GroupUiState(isLoading = false, group = group) }
        .stateIn(initialValue = GroupUiState(), scope = viewModelScope, started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MILLIS))

    fun onEvent(event: GroupEvent) {
        when (event) {
            is GroupEvent.AddMember -> addMember(event.name)
        }
    }

    private fun addMember(name: String) {
        if (!state.value.isMemberNameAvailable(name)) return
        viewModelScope.launch { groupRepository.addMember(groupId = groupId, name = name) }
    }
}
