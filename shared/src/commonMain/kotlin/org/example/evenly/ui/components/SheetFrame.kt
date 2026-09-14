package org.example.evenly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme

@Composable
fun SheetFrame(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = AppSpacing.screenPadding)
            .padding(bottom = AppSpacing.lg, top = AppSpacing.lg),
    ) {
        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTheme.textStyles.sectionHeading, text = title)
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        DottedDivider()
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        content()
    }
}

@Composable
fun SheetActions(primary: @Composable RowScope.() -> Unit, secondary: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        DottedDivider()
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            secondary()
            primary()
        }
    }
}
