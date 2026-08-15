package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.MainTab
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PulseIndigo
import com.example.ui.theme.PulseIndigoLight

@Composable
fun PulseBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    unreadMessagesCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("pulse_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = tab == currentTab
            val labelText = when (tab) {
                MainTab.CHATS -> stringResource(R.string.nav_chats)
                MainTab.UPDATES -> stringResource(R.string.nav_updates)
                MainTab.COMMUNITIES -> stringResource(R.string.nav_communities)
                MainTab.CHANNELS -> stringResource(R.string.nav_channels)
                MainTab.CALLS -> stringResource(R.string.nav_calls)
                MainTab.SETTINGS -> stringResource(R.string.nav_settings)
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (tab == MainTab.CHATS && unreadMessagesCount > 0) {
                                Badge(
                                    containerColor = PulseIndigo,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadMessagesCount > 99) "99+" else unreadMessagesCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = labelText,
                            tint = if (isSelected) PulseIndigoLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = {
                    Text(
                        text = labelText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PulseIndigoLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PulseIndigo.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_item_${tab.route}")
            )
        }
    }
}
