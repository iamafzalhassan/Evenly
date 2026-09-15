package org.example.evenly.ui.expenseeditor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.evenly.model.Member
import org.example.evenly.model.MemberId
import org.example.evenly.ui.components.AppListTile
import org.example.evenly.ui.components.AppModalSheet
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing

@Composable
fun MemberPickerSheet(title: String, members: List<Member>, onDismiss: () -> Unit, memberName: (Member) -> String, onSelect: (MemberId) -> Unit, selectedId: MemberId?, modifier: Modifier = Modifier) {
    AppModalSheet(modifier = modifier, onDismiss = onDismiss, title = title) { sheet ->
        Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            members.forEach { member ->
                AppListTile(
                    icon = Icons.Outlined.Person,
                    onClick = { sheet.closeThen { onSelect(member.id) } },
                    title = memberName(member),
                ) {
                    if (member.id == selectedId) {
                        Icon(contentDescription = null, imageVector = Icons.Outlined.Check, tint = AppColors.primary)
                    }
                }
            }
        }
    }
}
