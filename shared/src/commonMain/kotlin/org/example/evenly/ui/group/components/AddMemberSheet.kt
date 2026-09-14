package org.example.evenly.ui.group.components

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
import evenly.shared.generated.resources.action_add
import evenly.shared.generated.resources.action_cancel
import evenly.shared.generated.resources.add_member_title
import evenly.shared.generated.resources.error_member_name_taken
import evenly.shared.generated.resources.field_member_name
import kotlinx.coroutines.launch
import org.example.evenly.ui.components.AppTextField
import org.example.evenly.ui.components.PrimaryButton
import org.example.evenly.ui.components.SecondaryButton
import org.example.evenly.ui.components.SheetActions
import org.example.evenly.ui.components.SheetFrame
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberSheet(onDismiss: () -> Unit, isNameAvailable: (String) -> Boolean, onAdd: (String) -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable { mutableStateOf("") }
    val isTaken = name.isNotBlank() && !isNameAvailable(name)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = stringResource(Res.string.add_member_title)) {
            AppTextField(capitalization = KeyboardCapitalization.Words, label = stringResource(Res.string.field_member_name), onValueChange = { name = it }, value = name)
            if (isTaken) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.errorHint, text = stringResource(Res.string.error_member_name_taken))
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            SheetActions(
                primary = {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        isEnabled = isNameAvailable(name),
                        label = stringResource(Res.string.action_add),
                        onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onAdd(name.trim()) } },
                    )
                },
                secondary = {
                    SecondaryButton(modifier = Modifier.weight(1f), label = stringResource(Res.string.action_cancel), onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } })
                },
            )
        }
    }
}
