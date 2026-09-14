package org.example.evenly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme

private const val SNACK_MAX_LINES: Int = 2

enum class SnackTone { ERROR, NEUTRAL, SUCCESS }

@Composable
fun AppSnackbarHost(state: AppSnackbarState, modifier: Modifier = Modifier) {
    SnackbarHost(modifier = modifier, hostState = state.hostState) { data ->
        Snackbar(
            containerColor = when (state.tone) {
                SnackTone.ERROR -> AppColors.danger
                SnackTone.NEUTRAL -> AppColors.surfaceInverse
                SnackTone.SUCCESS -> AppColors.success
            },
            contentColor = AppColors.primaryOn,
            shape = RectangleShape,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.sm),
                maxLines = SNACK_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.textStyles.snack,
                text = data.visuals.message,
            )
        }
    }
}

@Composable
fun rememberAppSnackbarState(): AppSnackbarState {
    val hostState = remember { SnackbarHostState() }

    return remember(hostState) { AppSnackbarState(hostState) }
}

@Stable
class AppSnackbarState(val hostState: SnackbarHostState) {
    var tone: SnackTone by mutableStateOf(SnackTone.NEUTRAL)
        private set

    suspend fun showBrief(message: String) = show(message, SnackTone.NEUTRAL)

    suspend fun showError(message: String) = show(message, SnackTone.ERROR)

    suspend fun showSuccess(message: String) = show(message, SnackTone.SUCCESS)

    private suspend fun show(message: String, tone: SnackTone) {
        this.tone = tone
        hostState.currentSnackbarData?.dismiss()
        hostState.showSnackbar(duration = SnackbarDuration.Long, message = message)
    }
}
