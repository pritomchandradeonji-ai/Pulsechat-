package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val route: String,
    val titleResKey: String,
    val defaultTitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    CHATS(
        route = "chats",
        titleResKey = "nav_chats",
        defaultTitle = "Chats",
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline
    ),
    UPDATES(
        route = "updates",
        titleResKey = "nav_updates",
        defaultTitle = "Updates",
        selectedIcon = Icons.Filled.ChangeCircle,
        unselectedIcon = Icons.Outlined.ChangeCircle
    ),
    COMMUNITIES(
        route = "communities",
        titleResKey = "nav_communities",
        defaultTitle = "Communities",
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups
    ),
    CHANNELS(
        route = "channels",
        titleResKey = "nav_channels",
        defaultTitle = "Channels",
        selectedIcon = Icons.Filled.Campaign,
        unselectedIcon = Icons.Outlined.Campaign
    ),
    CALLS(
        route = "calls",
        titleResKey = "nav_calls",
        defaultTitle = "Calls",
        selectedIcon = Icons.Filled.Call,
        unselectedIcon = Icons.Outlined.Call
    ),
    SETTINGS(
        route = "settings",
        titleResKey = "nav_settings",
        defaultTitle = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}
