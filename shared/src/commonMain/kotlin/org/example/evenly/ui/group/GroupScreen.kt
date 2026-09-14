package org.example.evenly.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_add_member
import evenly.shared.generated.resources.group_expenses_empty_message
import evenly.shared.generated.resources.group_expenses_empty_title
import evenly.shared.generated.resources.group_expenses_heading
import evenly.shared.generated.resources.group_members_heading
import org.example.evenly.model.Group
import org.example.evenly.ui.components.AppListTile
import org.example.evenly.ui.components.AppTopBar
import org.example.evenly.ui.components.EmptyState
import org.example.evenly.ui.components.SecondaryButton
import org.example.evenly.ui.components.SectionHeader
import org.example.evenly.ui.group.components.AddMemberSheet
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupScreen(onBack: () -> Unit, viewModel: GroupViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isAddingMember by rememberSaveable { mutableStateOf(false) }
    val group = state.group

    Column(modifier = modifier.fillMaxSize().background(AppColors.surfaceBase)) {
        AppTopBar(onBack = onBack, title = group?.name.orEmpty())
        if (group != null) {
            GroupContent(modifier = Modifier.weight(1f), group = group, onAddMember = { isAddingMember = true })
        }
    }

    if (isAddingMember && group != null) {
        AddMemberSheet(
            isNameAvailable = state::isMemberNameAvailable,
            onAdd = { name ->
                isAddingMember = false
                viewModel.onEvent(GroupEvent.AddMember(name))
            },
            onDismiss = { isAddingMember = false },
        )
    }
}

@Composable
private fun GroupContent(onAddMember: () -> Unit, group: Group, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth().navigationBarsPadding(), contentPadding = PaddingValues(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.lg)) {
        item {
            SectionHeader(label = stringResource(Res.string.group_members_heading))
        }
        items(items = group.members, key = { member -> member.id.raw }) { member ->
            AppListTile(modifier = Modifier.padding(bottom = AppSpacing.sm), icon = Icons.Outlined.Person, title = member.name)
        }
        item {
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            SecondaryButton(modifier = Modifier.fillMaxWidth(), label = stringResource(Res.string.action_add_member), onClick = onAddMember)
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            SectionHeader(label = stringResource(Res.string.group_expenses_heading))
            EmptyState(
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg),
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                message = stringResource(Res.string.group_expenses_empty_message),
                title = stringResource(Res.string.group_expenses_empty_title),
            )
        }
    }
}
