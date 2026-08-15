package com.example.ui.screens.calls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallDirection
import com.example.data.model.CallRecord
import com.example.data.model.CallType
import com.example.ui.PulseViewModel
import com.example.ui.components.EmptyPlaceholder
import com.example.ui.components.PulseAvatar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    viewModel: PulseViewModel,
    onStartCall: (String, Long, CallType) -> Unit,
    modifier: Modifier = Modifier
) {
    val calls by viewModel.allCalls.collectAsState()
    var filterMissedOnly by remember { mutableStateOf(false) }

    val displayedCalls = if (filterMissedOnly) calls.filter { it.wasMissed } else calls

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calls",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    FilterChip(
                        selected = filterMissedOnly,
                        onClick = { filterMissedOnly = !filterMissedOnly },
                        label = { Text("Missed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseRose.copy(alpha = 0.2f),
                            selectedLabelColor = PulseRose
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onStartCall("Elena Rostova", 0xFF06B6D4, CallType.VOICE) },
                containerColor = PulseIndigo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_start_call")
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Start Call")
            }
        }
    ) { paddingValues ->
        if (displayedCalls.isEmpty()) {
            EmptyPlaceholder(
                icon = Icons.Outlined.Call,
                title = if (filterMissedOnly) "No Missed Calls" else "No Recent Calls",
                subtitle = "Make high-fidelity voice and HD video calls encrypted with WebRTC peer-to-peer protocols.",
                actionLabel = "Start a Call",
                onActionClick = { onStartCall("Elena Rostova", 0xFF06B6D4, CallType.VOICE) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Quick Call Link creator banner
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStartCall("Conference Room", 0xFF6366F1, CallType.VIDEO) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PulseEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create a call link",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Share a link for your Pulse video call",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Recent",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                items(displayedCalls) { call ->
                    val callDate = remember(call.timestamp) {
                        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(call.timestamp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStartCall(call.peerName, call.peerAvatarColorHex, call.callType) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("call_item_${call.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PulseAvatar(
                            name = call.peerName,
                            avatarColor = call.peerAvatarColorHex,
                            size = 50.dp
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = call.peerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (call.wasMissed) PulseRose else MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = when (call.direction) {
                                        CallDirection.INCOMING -> Icons.Default.CallReceived
                                        CallDirection.OUTGOING -> Icons.Default.CallMade
                                        CallDirection.MISSED -> Icons.Default.CallMissed
                                    },
                                    contentDescription = null,
                                    tint = if (call.wasMissed) PulseRose else PulseEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$callDate • ${if (call.durationSec > 0) "${call.durationSec / 60}m ${call.durationSec % 60}s" else "Missed"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { onStartCall(call.peerName, call.peerAvatarColorHex, call.callType) }
                        ) {
                            Icon(
                                imageVector = if (call.callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = "Call again",
                                tint = PulseCyanLight
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}
