package org.example.evenly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.evenly.model.GroupId
import org.example.evenly.ui.creategroup.CreateGroupScreen
import org.example.evenly.ui.creategroup.CreateGroupViewModel
import org.example.evenly.ui.group.GroupScreen
import org.example.evenly.ui.group.GroupViewModel
import org.example.evenly.ui.groups.GroupsScreen
import org.example.evenly.ui.groups.GroupsViewModel
import org.example.evenly.ui.navigation.CreateGroupRoute
import org.example.evenly.ui.navigation.GroupRoute
import org.example.evenly.ui.navigation.GroupsRoute
import org.example.evenly.ui.theme.AppColors
import org.example.evenly.ui.theme.EvenlyTheme

@Composable
fun App(graph: AppGraph) {
    EvenlyTheme {
        val navController = rememberNavController()

        NavHost(modifier = Modifier.fillMaxSize().background(AppColors.surfaceBase), navController = navController, startDestination = GroupsRoute) {
            composable<GroupsRoute> {
                GroupsScreen(
                    onCreateGroup = { navController.navigate(CreateGroupRoute) },
                    onOpenGroup = { groupId -> navController.navigate(GroupRoute(groupId = groupId.raw)) },
                    viewModel = viewModel { GroupsViewModel(graph.groupRepository) },
                )
            }
            composable<CreateGroupRoute> {
                CreateGroupScreen(
                    onBack = { navController.popBackStack() },
                    onGroupCreated = { groupId -> navController.navigate(GroupRoute(groupId = groupId.raw)) { popUpTo<CreateGroupRoute> { inclusive = true } } },
                    viewModel = viewModel { CreateGroupViewModel(graph.groupRepository) },
                )
            }
            composable<GroupRoute> { entry ->
                val route = entry.toRoute<GroupRoute>()

                GroupScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel { GroupViewModel(groupId = GroupId(route.groupId), groupRepository = graph.groupRepository) },
                )
            }
        }
    }
}
