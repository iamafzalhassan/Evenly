package org.example.evenly.data.sync

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val INVITE_NOT_FOUND_CODE: String = "P0002"

class SyncCoordinator internal constructor(scope: CoroutineScope, private val engine: SyncEngine) {
    companion object {
        val REQUEST_DEBOUNCE: Duration = 800.milliseconds
    }

    private val requests: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val mutableStatus: MutableStateFlow<SyncStatus> = MutableStateFlow(SyncStatus())

    private val mutex: Mutex = Mutex()

    val status: StateFlow<SyncStatus> = mutableStatus.asStateFlow()

    init {
        scope.launch {
            requests.collect {
                delay(REQUEST_DEBOUNCE)
                syncNow()
            }
        }
    }

    suspend fun syncNow(): Boolean = mutex.withLock {
        mutableStatus.update { status -> status.copy(isSyncing = true) }
        try {
            engine.sync()
            mutableStatus.update { status -> status.copy(isSyncing = false, failure = null, lastSyncedAt = Clock.System.now()) }
            true
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            mutableStatus.update { status -> status.copy(isSyncing = false, failure = exception.toFailure()) }
            false
        }
    }

    suspend fun joinGroup(code: String): JoinResult = mutex.withLock {
        mutableStatus.update { status -> status.copy(isSyncing = true) }
        try {
            val groupId = engine.joinGroup(code.trim())
            mutableStatus.update { status -> status.copy(isSyncing = false, failure = null, lastSyncedAt = Clock.System.now()) }
            JoinResult.Joined(groupId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: PostgrestRestException) {
            mutableStatus.update { status -> status.copy(isSyncing = false) }
            if (exception.code == INVITE_NOT_FOUND_CODE) JoinResult.NotFound else JoinResult.Offline
        } catch (exception: Exception) {
            mutableStatus.update { status -> status.copy(isSyncing = false, failure = exception.toFailure()) }
            JoinResult.Offline
        }
    }

    fun requestSync() {
        requests.tryEmit(Unit)
    }

    private fun Exception.toFailure(): SyncFailure = when (this) {
        is HttpRequestException -> SyncFailure.OFFLINE
        is RestException -> SyncFailure.SERVER
        else -> SyncFailure.OFFLINE
    }
}
