package org.example.evenly.ui.group

sealed interface GroupEvent {
    data class AddMember(val name: String) : GroupEvent
}
