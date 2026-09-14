package org.example.evenly.data.sync

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

enum class SyncFailure { OFFLINE, SERVER }

@Immutable
data class SyncStatus(val isSyncing: Boolean = false, val failure: SyncFailure? = null, val lastSyncedAt: Instant? = null)

sealed interface JoinResult {
    data class Joined(val groupId: String) : JoinResult

    data object NotFound : JoinResult

    data object Offline : JoinResult
}
