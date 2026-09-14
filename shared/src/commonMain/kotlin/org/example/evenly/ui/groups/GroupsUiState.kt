package org.example.evenly.ui.groups

import androidx.compose.runtime.Immutable
import org.example.evenly.model.Group

@Immutable
data class GroupsUiState(val isLoading: Boolean = true, val groups: List<Group> = emptyList())
