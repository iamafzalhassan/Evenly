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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.example.evenly.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalSheet(title: String, onDismiss: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.(sheet: AppSheetState) -> Unit) {
    val sheet = rememberAppSheetState()

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheet.sheetState) {
        SheetFrame(title = title) {
            content(sheet)
        }
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

@Composable
private fun SheetFrame(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberAppSheetState(): AppSheetState {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    return remember(scope, sheetState) { AppSheetState(scope = scope, sheetState = sheetState) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class AppSheetState internal constructor(private val scope: CoroutineScope, internal val sheetState: SheetState) {
    private var isClosing: Boolean = false

    fun closeThen(action: () -> Unit) {
        if (isClosing) return
        isClosing = true
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }
}
