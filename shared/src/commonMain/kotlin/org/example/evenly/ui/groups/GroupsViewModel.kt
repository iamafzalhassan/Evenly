package org.example.evenly.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.example.evenly.data.GroupRepository
import org.example.evenly.ui.STATE_STOP_TIMEOUT_MILLIS

class GroupsViewModel(groupRepository: GroupRepository) : ViewModel() {
    val state: StateFlow<GroupsUiState> = groupRepository.observeGroups()
        .map { groups -> GroupsUiState(isLoading = false, groups = groups) }
        .stateIn(initialValue = GroupsUiState(), scope = viewModelScope, started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MILLIS))
}
