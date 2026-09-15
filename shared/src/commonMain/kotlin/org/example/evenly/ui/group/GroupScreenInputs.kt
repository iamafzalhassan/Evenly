package org.example.evenly.ui.group

internal data class GroupScreenInputs(val isDeleting: Boolean = false, val isRefreshing: Boolean = false, val expenseQuery: String = "", val feedback: GroupFeedback? = null)
