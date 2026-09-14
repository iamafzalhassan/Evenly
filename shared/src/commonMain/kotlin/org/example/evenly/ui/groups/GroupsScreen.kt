package org.example.evenly.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import evenly.shared.generated.resources.Res
import evenly.shared.generated.resources.action_new_group
import evenly.shared.generated.resources.group_tile_subtitle
import evenly.shared.generated.resources.groups_empty_message
import evenly.shared.generated.resources.groups_empty_title
import evenly.shared.generated.resources.groups_title
import evenly.shared.generated.resources.member_count
import org.example.evenly.model.Group
import org.example.evenly.model.GroupId
import org.example.evenly.ui.components.AppListTile
import org.example.evenly.ui.components.AppTopBar
import org.example.evenly.ui.components.EmptyState
import org.example.evenly.ui.components.PrimaryButton
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.AppSpacing
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupsScreen(onCreateGroup: () -> Unit, onOpenGroup: (GroupId) -> Unit, viewModel: GroupsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().background(AppColors.surfaceBase)) {
        AppTopBar(title = stringResource(Res.string.groups_title)) {
            IconButton(onClick = onCreateGroup) {
                Icon(contentDescription = stringResource(Res.string.action_new_group), imageVector = Icons.Outlined.Add, tint = AppColors.primary)
            }
        }
        when {
            state.isLoading -> Unit
            state.groups.isEmpty() -> EmptyState(
                modifier = Modifier.weight(1f).fillMaxWidth().navigationBarsPadding().padding(horizontal = AppSpacing.screenPadding),
                icon = Icons.Outlined.Groups,
                message = stringResource(Res.string.groups_empty_message),
                title = stringResource(Res.string.groups_empty_title),
            ) {
                PrimaryButton(modifier = Modifier.fillMaxWidth(), label = stringResource(Res.string.action_new_group), onClick = onCreateGroup)
            }
            else -> GroupList(modifier = Modifier.weight(1f), groups = state.groups, onOpenGroup = onOpenGroup)
        }
    }
}

@Composable
private fun GroupList(groups: List<Group>, onOpenGroup: (GroupId) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = AppSpacing.xl, end = AppSpacing.screenPadding, start = AppSpacing.screenPadding, top = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        items(items = groups, key = { group -> group.id.raw }) { group ->
            val memberCount = pluralStringResource(Res.plurals.member_count, group.members.size, group.members.size)

            AppListTile(
                icon = Icons.Outlined.Groups,
                onClick = { onOpenGroup(group.id) },
                subtitle = stringResource(Res.string.group_tile_subtitle, memberCount, group.currency.name),
                title = group.name,
            )
        }
    }
}
