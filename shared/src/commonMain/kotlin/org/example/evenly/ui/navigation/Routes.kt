package org.example.evenly.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CreateGroupRoute

@Serializable
data class GroupRoute(val groupId: String)

@Serializable
data object GroupsRoute
