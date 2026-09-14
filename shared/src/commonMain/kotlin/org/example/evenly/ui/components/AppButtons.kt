package org.example.evenly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme

@Composable
fun PrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    Button(
        modifier = modifier.height(AppSpacing.buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primary,
            contentColor = AppColors.primaryOn,
            disabledContainerColor = AppColors.surfaceSunken,
            disabledContentColor = AppColors.textDisabled,
        ),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg),
        elevation = null,
        enabled = isEnabled,
        onClick = onClick,
        shape = RoundedCornerShape(AppSpacing.radiusButton),
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.button.copy(color = if (isEnabled) AppColors.primaryOn else AppColors.textDisabled), text = label)
    }
}

@Composable
fun SecondaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    OutlinedButton(
        modifier = modifier.height(AppSpacing.buttonHeight),
        border = BorderStroke(AppSpacing.hairline, if (isEnabled) AppColors.primary else AppColors.divider),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.primary, disabledContentColor = AppColors.textDisabled),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg),
        enabled = isEnabled,
        onClick = onClick,
        shape = RoundedCornerShape(AppSpacing.radiusButton),
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.button.copy(color = if (isEnabled) AppColors.primary else AppColors.textDisabled), text = label)
    }
}

@Composable
fun DangerButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    OutlinedButton(
        modifier = modifier.height(AppSpacing.buttonHeight),
        border = BorderStroke(AppSpacing.hairline, if (isEnabled) AppColors.danger else AppColors.divider),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.danger, disabledContentColor = AppColors.textDisabled),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg),
        enabled = isEnabled,
        onClick = onClick,
        shape = RoundedCornerShape(AppSpacing.radiusButton),
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.button.copy(color = if (isEnabled) AppColors.danger else AppColors.textDisabled), text = label)
    }
}
