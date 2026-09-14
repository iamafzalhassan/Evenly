package org.example.evenly.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_cancel
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmSheet(confirmLabel: String, message: String, title: String, onConfirm: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier, isDestructive: Boolean = false) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = title) {
            Text(modifier = Modifier.fillMaxWidth(), style = AppTheme.textStyles.body, text = message)
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            SheetActions(
                primary = {
                    if (isDestructive) {
                        DangerButton(modifier = Modifier.weight(1f), label = confirmLabel, onClick = { sheetState.hideThen(action = onConfirm, scope = scope) })
                    } else {
                        PrimaryButton(modifier = Modifier.weight(1f), label = confirmLabel, onClick = { sheetState.hideThen(action = onConfirm, scope = scope) })
                    }
                },
                secondary = {
                    SecondaryButton(modifier = Modifier.weight(1f), label = stringResource(Res.string.action_cancel), onClick = { sheetState.hideThen(action = onDismiss, scope = scope) })
                },
            )
        }
    }
}
