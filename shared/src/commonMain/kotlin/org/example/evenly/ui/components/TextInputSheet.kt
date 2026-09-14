package org.example.evenly.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_cancel
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputSheet(
    confirmLabel: String,
    initialValue: String,
    label: String,
    title: String,
    onDismiss: () -> Unit,
    errorMessage: (String) -> String?,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var value by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    val error = if (value.isBlank()) null else errorMessage(value)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = title) {
            AppTextField(capitalization = capitalization, label = label, onValueChange = { value = it }, value = value)
            if (error != null) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.errorHint, text = error)
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            SheetActions(
                primary = {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        isEnabled = value.isNotBlank() && error == null,
                        label = confirmLabel,
                        onClick = { sheetState.hideThen(action = { onConfirm(value.trim()) }, scope = scope) },
                    )
                },
                secondary = {
                    SecondaryButton(modifier = Modifier.weight(1f), label = stringResource(Res.string.action_cancel), onClick = { sheetState.hideThen(action = onDismiss, scope = scope) })
                },
            )
        }
    }
}
