package org.example.evenly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    keyboardType: KeyboardType = KeyboardType.Text,
    textAlign: TextAlign = TextAlign.Start,
) {
    val focusManager = LocalFocusManager.current

    FieldFrame(modifier = modifier, label = label, textAlign = textAlign) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(AppColors.primary),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                imeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
                keyboardType = keyboardType,
            ),
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = AppTheme.textStyles.fieldValue.copy(textAlign = textAlign),
            value = value,
        )
    }
}

@Composable
fun AppStaticField(label: String, value: String, modifier: Modifier = Modifier) {
    FieldFrame(modifier = modifier, label = label, textAlign = TextAlign.Start) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.textStyles.fieldValue,
            text = value,
        )
    }
}

@Composable
internal fun FieldFrame(label: String, textAlign: TextAlign, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.controlHeight)
            .background(color = AppColors.surfaceField, shape = RoundedCornerShape(AppSpacing.radiusField))
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.textStyles.fieldLabel.copy(textAlign = textAlign),
            text = label,
        )
        content()
    }
}
