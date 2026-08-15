package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PulseViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConsoleScreen(
    viewModel: PulseViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAppName by viewModel.appName.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()

    var newAppNameInput by remember { mutableStateOf(currentAppName) }
    var broadcastMessage by remember { mutableStateOf("") }
    var userSearchQuery by remember { mutableStateOf("") }
    var suspendedUsersCount by remember { mutableStateOf(12) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PulseIndigoLight)
                        Text(
                            text = "Admin Health Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Telemetry Cards Grid
            item {
                Text(
                    text = "System Real-Time Telemetry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        title = "Active Users",
                        value = "${adminStats.activeUsersCount}",
                        badge = "+14.2% today",
                        badgeColor = PulseEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Throughput",
                        value = "${adminStats.messagesPerSecond} msg/s",
                        badge = "18ms latency",
                        badgeColor = PulseCyanLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        title = "Live Calls (WebRTC)",
                        value = "${adminStats.activeCallsCount}",
                        badge = "Mesh HD",
                        badgeColor = PulseIndigoLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Uptime Health",
                        value = "${adminStats.uptimePercentage}%",
                        badge = "All clusters green",
                        badgeColor = PulseEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Real-Time Throughput Graph
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Network Activity (24 Hours)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Peak 5.4k/s", color = PulseCyanLight, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            val points = listOf(0.2f, 0.4f, 0.35f, 0.7f, 0.5f, 0.85f, 0.65f, 0.95f, 0.8f, 1.0f)
                            val path = Path()
                            val stepX = size.width / (points.size - 1)

                            points.forEachIndexed { index, p ->
                                val x = index * stepX
                                val y = size.height - (p * size.height * 0.85f)
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }

                            drawPath(
                                path = path,
                                color = PulseIndigoLight,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }
            }

            // App Rebranding Section (Mandated requirement)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.BrandingWatermark, contentDescription = null, tint = PulseIndigoLight)
                            Text("Application Branding Manager", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "Dynamically customize the global app name across headers, invite links, and notifications.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = newAppNameInput,
                            onValueChange = { newAppNameInput = it },
                            label = { Text("App Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_app_name_input")
                        )

                        Button(
                            onClick = {
                                viewModel.updateAppName(newAppNameInput)
                                Toast.makeText(context, "App name updated to \"$newAppNameInput\"!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_save_app_name_btn")
                        ) {
                            Text("Update App Branding", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // User Moderation & Suspension
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = PulseRose)
                            Text("User Moderation & Safety", fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(
                            value = userSearchQuery,
                            onValueChange = { userSearchQuery = it },
                            placeholder = { Text("Search by user ID or handle...") },
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    suspendedUsersCount += 1
                                    Toast.makeText(context, "User suspended for TOS violation", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PulseRose),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Suspend User", color = Color.White)
                            }
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "All ${adminStats.spamReportsPending} pending reports cleared", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Resolve Reports")
                            }
                        }
                    }
                }
            }

            // Broadcast Announcement Dispatcher
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = PulseCyanLight)
                            Text("System Broadcast Push", fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(
                            value = broadcastMessage,
                            onValueChange = { broadcastMessage = it },
                            placeholder = { Text("Write urgent system-wide announcement...") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (broadcastMessage.isNotBlank()) {
                                    Toast.makeText(context, "Broadcast dispatched to ${adminStats.activeUsersCount} devices!", Toast.LENGTH_LONG).show()
                                    broadcastMessage = ""
                                }
                            },
                            enabled = broadcastMessage.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PulseCyanDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dispatch Push Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
