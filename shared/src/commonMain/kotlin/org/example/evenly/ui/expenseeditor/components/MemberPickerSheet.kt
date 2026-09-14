package org.example.evenly.ui.expenseeditor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.example.evenly.model.Member
import org.example.evenly.model.MemberId
import org.example.evenly.ui.components.AppListTile
import org.example.evenly.ui.components.SheetFrame
import org.example.evenly.ui.components.hideThen
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberPickerSheet(title: String, members: List<Member>, onDismiss: () -> Unit, onSelect: (MemberId) -> Unit, selectedId: MemberId?, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(modifier = modifier, containerColor = AppColors.surfaceCard, onDismissRequest = onDismiss, sheetState = sheetState) {
        SheetFrame(title = title) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                members.forEach { member ->
                    AppListTile(
                        icon = Icons.Outlined.Person,
                        onClick = { sheetState.hideThen(action = { onSelect(member.id) }, scope = scope) },
                        title = member.name,
                    ) {
                        if (member.id == selectedId) {
                            Icon(contentDescription = null, imageVector = Icons.Outlined.Check, tint = AppColors.primary)
                        }
                    }
                }
            }
        }
    }
}
