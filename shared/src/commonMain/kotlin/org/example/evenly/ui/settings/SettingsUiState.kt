package org.example.evenly.ui.settings

import androidx.compose.runtime.Immutable
import org.example.evenly.data.sync.SyncStatus

@Immutable
data class SettingsUiState(val isBiometricLockEnabled: Boolean = false, val isLoading: Boolean = true, val isSaveFailed: Boolean = false, val syncStatus: SyncStatus = SyncStatus())
