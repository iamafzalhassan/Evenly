package org.example.evenly.ui.group

import androidx.compose.runtime.Immutable
import org.example.evenly.model.Group

@Immutable
data class GroupUiState(val isLoading: Boolean = true, val group: Group? = null) {
    fun isMemberNameAvailable(name: String): Boolean {
        val trimmed = name.trim()
        val members = group?.members ?: return false
        return trimmed.isNotEmpty() && members.none { it.name.equals(trimmed, ignoreCase = true) }
    }
}
