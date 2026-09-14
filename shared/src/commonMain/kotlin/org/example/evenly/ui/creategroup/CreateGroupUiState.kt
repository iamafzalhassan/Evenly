package org.example.evenly.ui.creategroup

import androidx.compose.runtime.Immutable
import org.example.evenly.model.Currency
import org.example.evenly.model.GroupId

@Immutable
data class CreateGroupUiState(
    val isSaveFailed: Boolean = false,
    val isSaving: Boolean = false,
    val name: String = "",
    val memberNames: List<String> = listOf("", ""),
    val currency: Currency = Currency.LKR,
    val createdGroupId: GroupId? = null,
) {
    companion object {
        const val MIN_MEMBERS: Int = 2
    }

    val canCreate: Boolean get() = !isSaving && name.isNotBlank() && trimmedMemberNames.size >= MIN_MEMBERS && !hasDuplicateMembers
    val canRemoveMember: Boolean get() = memberNames.size > MIN_MEMBERS
    val hasDuplicateMembers: Boolean get() = trimmedMemberNames.map { it.lowercase() }.toSet().size != trimmedMemberNames.size

    val trimmedMemberNames: List<String> get() = memberNames.map { it.trim() }.filter { it.isNotEmpty() }
}
