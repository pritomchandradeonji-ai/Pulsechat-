package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrivacyLevel
import com.example.ui.PulseViewModel
import com.example.ui.components.PulseAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PulseViewModel,
    onOpenAdminConsole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val privacySettings by viewModel.privacySettings.collectAsState()
    val securitySettings by viewModel.securitySettings.collectAsState()
    val linkedDevices by viewModel.linkedDevices.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var showProfileEditor by remember { mutableStateOf(false) }
    var showQRCodeDialog by remember { mutableStateOf(false) }
    var showLinkedDevicesDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showProfileEditor = true }
                        .testTag("settings_profile_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PulseAvatar(
                            name = currentUser.name,
                            avatarColor = currentUser.avatarColorHex,
                            size = 60.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "@${currentUser.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PulseCyanLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currentUser.bio,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { showQRCodeDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Profile QR Code",
                                tint = PulseIndigoLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Settings Navigation Sections
            item {
                SettingsSectionHeader("Security & Connectivity")

                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    iconColor = PulseEmerald,
                    title = "Privacy & Encryption",
                    subtitle = "Last seen, profile photo, read receipts, E2EE",
                    onClick = { showPrivacyDialog = true }
                )

                SettingsRowItem(
                    icon = Icons.Default.Security,
                    iconColor = PulseIndigoLight,
                    title = "Two-Step Verification & Security",
                    subtitle = if (securitySettings.twoStepVerificationEnabled) "Enabled • Protected with PIN" else "Disabled",
                    onClick = { showSecurityDialog = true }
                )

                SettingsRowItem(
                    icon = Icons.Default.Devices,
                    iconColor = PulseCyanLight,
                    title = "Linked Devices",
                    subtitle = "${linkedDevices.size} active sessions (Web, macOS, Windows)",
                    onClick = { showLinkedDevicesDialog = true }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            }

            item {
                SettingsSectionHeader("Preferences")

                SettingsRowItem(
                    icon = Icons.Default.Storage,
                    iconColor = PulseAmber,
                    title = "Storage and Data",
                    subtitle = "Network usage, auto-download, cache management",
                    onClick = { showStorageDialog = true }
                )

                SettingsRowItem(
                    icon = Icons.Default.Language,
                    iconColor = Color(0xFF8B5CF6),
                    title = "Language",
                    subtitle = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            }

            item {
                SettingsSectionHeader("System & Administration")

                // Admin Console button
                SettingsRowItem(
                    icon = Icons.Default.AdminPanelSettings,
                    iconColor = PulseIndigo,
                    title = "Pulse Admin Console",
                    subtitle = "App rebranding, server telemetry, user moderation",
                    onClick = onOpenAdminConsole,
                    isHighlighted = true
                )

                SettingsRowItem(
                    icon = Icons.Default.Info,
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = "About PulseChat",
                    subtitle = "Version 3.4.0 (Build 2026.08) • Production",
                    onClick = {
                        Toast.makeText(context, "PulseChat v3.4.0 • Zero-Knowledge Core", Toast.LENGTH_SHORT).show()
                    }
                )

                SettingsRowItem(
                    icon = Icons.Default.Logout,
                    iconColor = PulseRose,
                    title = "Log Out",
                    subtitle = "End active session on this device",
                    onClick = { viewModel.logout() }
                )
            }
        }
    }

    // Profile Editor Dialog
    if (showProfileEditor) {
        var name by remember { mutableStateOf(currentUser.name) }
        var username by remember { mutableStateOf(currentUser.username) }
        var bio by remember { mutableStateOf(currentUser.bio) }
        var phone by remember { mutableStateOf(currentUser.phone) }
        var email by remember { mutableStateOf(currentUser.email) }

        AlertDialog(
            onDismissRequest = { showProfileEditor = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("About / Bio") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(name, username, bio, phone, email)
                        showProfileEditor = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileEditor = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // QR Code Dialog
    if (showQRCodeDialog) {
        AlertDialog(
            onDismissRequest = { showQRCodeDialog = false },
            title = { Text("Profile QR Code", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PulseAvatar(name = currentUser.name, avatarColor = currentUser.avatarColorHex, size = 64.dp)
                    Text(text = currentUser.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = "@${currentUser.username}", color = PulseCyanLight, style = MaterialTheme.typography.bodySmall)

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(160.dp)
                            .padding(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(130.dp))
                        }
                    }
                    Text(
                        text = "Scan this code to start a secure chat with ${currentUser.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQRCodeDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Linked Devices Modal
    if (showLinkedDevicesDialog) {
        AlertDialog(
            onDismissRequest = { showLinkedDevicesDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = PulseCyanLight)
                    Text("Linked Devices", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "QR Scanner active. Scan QR code on web.pulsechat.io", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo)
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Link a Device (Scan QR)", color = Color.White)
                    }

                    Text("Active Sessions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)

                    linkedDevices.forEach { device ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (device.platform.contains("Android")) Icons.Default.PhoneAndroid else Icons.Default.Laptop,
                                    contentDescription = null,
                                    tint = if (device.isCurrentDevice) PulseEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = device.deviceName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${device.location} • ${device.lastActive}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (!device.isCurrentDevice) {
                                    IconButton(
                                        onClick = { viewModel.terminateSession(device.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Terminate", tint = PulseRose)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLinkedDevicesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Privacy Settings Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Controls", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Read Receipts", fontWeight = FontWeight.SemiBold)
                            Text("If turned off, you won't send or receive read receipts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = privacySettings.readReceiptsEnabled,
                            onCheckedChange = { viewModel.toggleReadReceipts() }
                        )
                    }

                    Divider()

                    Text("Last Seen & Online Status", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = privacySettings.lastSeenPrivacy == PrivacyLevel.EVERYONE,
                            onClick = { viewModel.setPrivacyLastSeen(PrivacyLevel.EVERYONE) },
                            label = { Text("Everyone") }
                        )
                        FilterChip(
                            selected = privacySettings.lastSeenPrivacy == PrivacyLevel.MY_CONTACTS,
                            onClick = { viewModel.setPrivacyLastSeen(PrivacyLevel.MY_CONTACTS) },
                            label = { Text("Contacts") }
                        )
                        FilterChip(
                            selected = privacySettings.lastSeenPrivacy == PrivacyLevel.NOBODY,
                            onClick = { viewModel.setPrivacyLastSeen(PrivacyLevel.NOBODY) },
                            label = { Text("Nobody") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Security & Two-Step Verification Dialog
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = { Text("Security & PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Two-Step Verification", fontWeight = FontWeight.SemiBold)
                            Text("Require a 6-digit security PIN when logging in", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = securitySettings.twoStepVerificationEnabled,
                            onCheckedChange = { viewModel.toggleTwoStepVerification() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biometric App Lock", fontWeight = FontWeight.SemiBold)
                            Text("Unlock Pulse with fingerprint or face ID", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = securitySettings.biometricLockEnabled,
                            onCheckedChange = { viewModel.toggleBiometricLock() }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSecurityDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Storage & Cache Clear Dialog
    if (showStorageDialog) {
        AlertDialog(
            onDismissRequest = { showStorageDialog = false },
            title = { Text("Storage and Data Usage", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Device Cache: 284 MB", fontWeight = FontWeight.Bold)
                            Text("Media & Documents: 1.2 GB", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Cleared 284 MB temporary cache!", Toast.LENGTH_SHORT).show()
                            showStorageDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PulseRose),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Temporary Cache", color = Color.White)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStorageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        val languages = listOf("English", "Bengali (বাংলা)", "Arabic (العربية)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select App Language", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = lang, fontWeight = FontWeight.Medium)
                            if (currentLanguage == lang) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = PulseIndigoLight)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    Surface(
        color = if (isHighlighted) PulseIndigo.copy(alpha = 0.08f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
