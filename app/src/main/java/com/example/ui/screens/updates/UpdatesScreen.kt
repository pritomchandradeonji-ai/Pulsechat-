package com.example.ui.screens.updates

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StatusStory
import com.example.data.model.StoryType
import com.example.ui.PulseViewModel
import com.example.ui.components.PulseAvatar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.allStatuses.collectAsState()
    val activeStory by viewModel.activeStoryView.collectAsState()

    var showCreateStoryDialog by remember { mutableStateOf(false) }
    var showViewersDialog by remember { mutableStateOf(false) }

    val myStatus = statuses.find { it.isMyStatus }
    val contactStatuses = statuses.filter { !it.isMyStatus }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Updates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showCreateStoryDialog = true },
                        modifier = Modifier.testTag("create_story_top_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "Add Status")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { showCreateStoryDialog = true },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Text status")
                }
                FloatingActionButton(
                    onClick = { showCreateStoryDialog = true },
                    containerColor = PulseIndigo,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_story")
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera status")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Status Header Section
            item {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // My Status Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (myStatus != null) {
                                viewModel.openStory(myStatus)
                            } else {
                                showCreateStoryDialog = true
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("my_status_row"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        PulseAvatar(
                            name = "Alex Vance",
                            avatarColor = 0xFF6366F1,
                            size = 56.dp,
                            hasStory = myStatus != null
                        )
                        if (myStatus == null) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(PulseIndigo)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (myStatus != null) {
                                "${myStatus.viewersCount} viewers • Tap to view"
                            } else {
                                "Tap to add status update"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (myStatus != null) {
                        IconButton(onClick = { showViewersDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Viewers",
                                tint = PulseIndigoLight
                            )
                        }
                    }
                }

                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                )
            }

            // Recent Updates Section
            item {
                Text(
                    text = "Recent updates",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(contactStatuses) { story ->
                val timeAgo = remember(story.timestamp) {
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(story.timestamp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openStory(story) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("story_item_${story.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PulseAvatar(
                        name = story.userName,
                        avatarColor = story.userAvatarColorHex,
                        size = 54.dp,
                        hasStory = true
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = story.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Interactive Full-screen Story Player Modal
    activeStory?.let { story ->
        StoryPlayerModal(
            story = story,
            onClose = { viewModel.openStory(null) },
            onReply = { replyText ->
                // Simulate reply
                viewModel.openStory(null)
            }
        )
    }

    // Create Story Modal
    if (showCreateStoryDialog) {
        CreateStoryDialog(
            onDismiss = { showCreateStoryDialog = false },
            onPost = { text, gradientHex ->
                viewModel.postStory(text, StoryType.TEXT, gradientHex)
                showCreateStoryDialog = false
            }
        )
    }

    // Viewers List Dialog
    if (showViewersDialog && myStatus != null) {
        AlertDialog(
            onDismissRequest = { showViewersDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = PulseIndigoLight)
                    Text("Viewed by ${myStatus.viewersCount}", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    myStatus.viewers.forEach { viewerName ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PulseAvatar(name = viewerName, size = 36.dp)
                            Text(text = viewerName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showViewersDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun StoryPlayerModal(
    story: StatusStory,
    onClose: () -> Unit,
    onReply: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0.6f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(story.backgroundGradientHex))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Progress Bars
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User Info header & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PulseAvatar(
                        name = story.userName,
                        avatarColor = story.userAvatarColorHex,
                        size = 40.dp
                    )
                    Column {
                        Text(
                            text = story.userName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "24h Expiring Story",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Main Story Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.content,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
            }

            // Quick emoji reactions & Reply bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val storyEmojis = listOf("❤️", "🔥", "👏", "😂", "😮", "🎉")
                storyEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 26.sp,
                        modifier = Modifier
                            .clickable {
                                onReply(emoji)
                                onClose()
                            }
                            .padding(6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Reply to ${story.userName}...", color = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                    )
                )

                if (replyText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onReply(replyText)
                            onClose()
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PulseIndigo)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateStoryDialog(
    onDismiss: () -> Unit,
    onPost: (String, Long) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedGradientHex by remember { mutableStateOf(0xFF4338CA) }

    val gradients = listOf(
        0xFF4338CA, // Indigo
        0xFF0E7490, // Cyan
        0xFF065F46, // Emerald
        0xFF9F1239, // Rose
        0xFF7C2D12  // Amber
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Status Story", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(selectedGradientHex))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (text.isBlank()) "Type your story..." else text,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Status text") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    gradients.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .clickable { selectedGradientHex = hex }
                                .padding(2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onPost(text.trim(), selectedGradientHex)
                    }
                },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo)
            ) {
                Text("Share to Status", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
