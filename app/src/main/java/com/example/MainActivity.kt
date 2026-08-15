package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CallType
import com.example.ui.PulseViewModel
import com.example.ui.components.PulseBottomNavigationBar
import com.example.ui.navigation.MainTab
import com.example.ui.screens.admin.AdminConsoleScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.calls.ActiveCallScreen
import com.example.ui.screens.calls.CallsScreen
import com.example.ui.screens.channels.ChannelsScreen
import com.example.ui.screens.chats.ChatListScreen
import com.example.ui.screens.chats.ConversationScreen
import com.example.ui.screens.communities.CommunitiesScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.updates.UpdatesScreen
import com.example.ui.theme.PulseChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulseChatTheme {
                val viewModel: PulseViewModel = viewModel()
                PulseChatApp(viewModel)
            }
        }
    }
}

@Composable
fun PulseChatApp(viewModel: PulseViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val activeChats by viewModel.activeChats.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.CHATS) }
    var openedChatId by remember { mutableStateOf<String?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var isAdminOpen by remember { mutableStateOf(false) }

    val totalUnread = remember(activeChats) {
        activeChats.sumOf { it.unreadCount }
    }

    if (!isAuthenticated) {
        AuthScreen(viewModel = viewModel)
    } else if (activeCall != null) {
        // Fullscreen Active WebRTC Call overlay
        ActiveCallScreen(
            session = activeCall!!,
            viewModel = viewModel
        )
    } else if (openedChatId != null) {
        // Direct / Group Conversation Screen
        BackHandler {
            openedChatId = null
        }
        ConversationScreen(
            chatId = openedChatId!!,
            viewModel = viewModel,
            onBack = { openedChatId = null },
            onStartVoiceCall = { peerName, color ->
                viewModel.startCall(peerName, color, CallType.VOICE)
            },
            onStartVideoCall = { peerName, color ->
                viewModel.startCall(peerName, color, CallType.VIDEO)
            }
        )
    } else if (isSearchOpen) {
        // Global Search Screen
        BackHandler {
            isSearchOpen = false
        }
        GlobalSearchScreen(
            viewModel = viewModel,
            onChatClick = { chatId ->
                isSearchOpen = false
                openedChatId = chatId
            },
            onBack = { isSearchOpen = false }
        )
    } else if (isAdminOpen) {
        // Admin Health & Rebranding Console
        BackHandler {
            isAdminOpen = false
        }
        AdminConsoleScreen(
            viewModel = viewModel,
            onBack = { isAdminOpen = false }
        )
    } else {
        // Main Tab Shell with Bottom Navigation
        Scaffold(
            bottomBar = {
                PulseBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it },
                    unreadMessagesCount = totalUnread
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    MainTab.CHATS -> {
                        ChatListScreen(
                            viewModel = viewModel,
                            onChatClick = { chatId -> openedChatId = chatId },
                            onSearchClick = { isSearchOpen = true },
                            onArchivedClick = { viewModel.setChatFilter("Archived") }
                        )
                    }
                    MainTab.UPDATES -> {
                        UpdatesScreen(viewModel = viewModel)
                    }
                    MainTab.COMMUNITIES -> {
                        CommunitiesScreen(
                            viewModel = viewModel,
                            onGroupClick = { _, groupName ->
                                viewModel.createChat(groupName, true, "Community Sub-group")
                                currentTab = MainTab.CHATS
                            }
                        )
                    }
                    MainTab.CHANNELS -> {
                        ChannelsScreen(viewModel = viewModel)
                    }
                    MainTab.CALLS -> {
                        CallsScreen(
                            viewModel = viewModel,
                            onStartCall = { peerName, color, type ->
                                viewModel.startCall(peerName, color, type)
                            }
                        )
                    }
                    MainTab.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onOpenAdminConsole = { isAdminOpen = true }
                        )
                    }
                }
            }
        }
    }
}
