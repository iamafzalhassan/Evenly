package org.example.evenly.ui.group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_close
import evenly.shared.generated.resources.action_copy_code
import evenly.shared.generated.resources.invite_message
import evenly.shared.generated.resources.invite_pending
import evenly.shared.generated.resources.invite_title
import org.example.evenly.ui.components.PrimaryButton
import org.example.evenly.ui.components.SecondaryButton
import org.example.evenly.ui.components.SheetActions
import org.example.evenly.ui.components.SheetFrame
import org.example.evenly.ui.components.hideThen
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

private const val INVITE_CODE_TRACKING: Float = 4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteSheet(inviteCode: String?, onCopied: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = stringResource(Res.string.invite_title)) {
            Text(modifier = Modifier.fillMaxWidth(), style = AppTheme.textStyles.body, text = stringResource(if (inviteCode == null) Res.string.invite_pending else Res.string.invite_message))
            if (inviteCode != null) {
                Spacer(modifier = Modifier.height(AppSpacing.lg))
                Box(modifier = Modifier.fillMaxWidth().background(color = AppColors.surfaceField, shape = RoundedCornerShape(AppSpacing.radiusCard)).padding(vertical = AppSpacing.lg), contentAlignment = Alignment.Center) {
                    Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.totalsValueBold.copy(letterSpacing = INVITE_CODE_TRACKING.sp), text = inviteCode)
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            SheetActions(
                primary = {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        isEnabled = inviteCode != null,
                        label = stringResource(Res.string.action_copy_code),
                        onClick = {
                            if (inviteCode != null) {
                                clipboard.setText(AnnotatedString(inviteCode))
                                sheetState.hideThen(action = onCopied, scope = scope)
                            }
                        },
                    )
                },
                secondary = {
                    SecondaryButton(modifier = Modifier.weight(1f), label = stringResource(Res.string.action_close), onClick = { sheetState.hideThen(action = onDismiss, scope = scope) })
                },
            )
        }
    }
}
