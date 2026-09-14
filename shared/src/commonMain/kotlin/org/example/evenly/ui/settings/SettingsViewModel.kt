package org.example.evenly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.evenly.data.SettingsRepository
import org.example.evenly.data.sync.SyncCoordinator
import org.example.evenly.ui.STATE_STOP_TIMEOUT_MILLIS

class SettingsViewModel(private val settingsRepository: SettingsRepository, private val syncCoordinator: SyncCoordinator) : ViewModel() {
    private val saveFailed: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: StateFlow<SettingsUiState> = combine(settingsRepository.observeBiometricLockEnabled(), saveFailed, syncCoordinator.status) { isEnabled, isSaveFailed, syncStatus ->
        SettingsUiState(isBiometricLockEnabled = isEnabled, isLoading = false, isSaveFailed = isSaveFailed, syncStatus = syncStatus)
    }.stateIn(initialValue = SettingsUiState(), scope = viewModelScope, started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MILLIS))

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.DismissSaveFailure -> saveFailed.value = false
            is SettingsEvent.SetBiometricLock -> setBiometricLock(event.isEnabled)
            SettingsEvent.SyncNow -> viewModelScope.launch { syncCoordinator.syncNow() }
        }
    }

    private fun setBiometricLock(isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setBiometricLockEnabled(isEnabled)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                saveFailed.value = true
            }
        }
    }
}
