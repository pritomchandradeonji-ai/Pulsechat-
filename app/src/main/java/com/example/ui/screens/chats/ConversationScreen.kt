package com.example.ui.screens.chats

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.PulseViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chatId: String,
    viewModel: PulseViewModel,
    onBack: () -> Unit,
    onStartVoiceCall: (String, Long) -> Unit,
    onStartVideoCall: (String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentChat by viewModel.currentChat.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
    val audioDurationSeconds by viewModel.audioRecordingSeconds.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForOptions by remember { mutableStateOf<Message?>(null) }
    var editingMessage by remember { mutableStateOf<Message?>(null) }
    var showMediaSheet by remember { mutableStateOf(false) }
    var showE2EEDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        viewModel.selectChat(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("conversation_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showE2EEDialog = true }
                    ) {
                        PulseAvatar(
                            name = currentChat?.name ?: "Chat",
                            avatarColor = currentChat?.avatarColorHex ?: 0xFF6366F1,
                            size = 38.dp,
                            isOnline = currentChat?.isOnline ?: false
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentChat?.name ?: "Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = PulseEmerald,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = if (currentChat?.type == ChatType.GROUP) {
                                        "${currentChat?.groupMemberCount ?: 0} members"
                                    } else if (currentChat?.isOnline == true) {
                                        "online • E2EE"
                                    } else {
                                        "last seen recently • E2EE"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            currentChat?.let { onStartVideoCall(it.name, it.avatarColorHex) }
                        },
                        modifier = Modifier.testTag("start_video_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = PulseCyanLight
                        )
                    }
                    IconButton(
                        onClick = {
                            currentChat?.let { onStartVoiceCall(it.name, it.avatarColorHex) }
                        },
                        modifier = Modifier.testTag("start_voice_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = PulseCyanLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Pinned Message banner if any
            val pinnedMessage = messages.findLast { it.isPinned }
            if (pinnedMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = PulseIndigoLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pinned Message",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseIndigoLight
                            )
                            Text(
                                text = pinnedMessage.text,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleMessagePin(pinnedMessage.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Unpin", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Disappearing Messages Notice
            if (currentChat?.disappearingMessageTimeSec != null && currentChat!!.disappearingMessageTimeSec > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = PulseCyanLight, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Disappearing messages are turned on (24 hours)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    MessageBubble(
                        message = msg,
                        onLongClick = { selectedMessageForOptions = it },
                        onReactionClick = { m, emoji -> viewModel.toggleReaction(m, emoji) },
                        onReplyClick = { replyingToMessage = it }
                    )
                }
            }

            // Reply banner if active
            replyingToMessage?.let { replyTarget ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(32.dp)
                                .background(PulseIndigo)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyTarget.senderName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseIndigoLight
                            )
                            Text(
                                text = replyTarget.text,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { replyingToMessage = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel reply")
                        }
                    }
                }
            }

            // Bottom Input Section: Audio recording bar vs Text entry
            if (isRecordingAudio) {
                // Audio Recording active bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(PulseRose)
                            )
                            Text(
                                text = "Recording: ${audioDurationSeconds / 60}:${(audioDurationSeconds % 60).toString().padStart(2, '0')}",
                                fontWeight = FontWeight.Bold,
                                color = PulseRose
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(onClick = { viewModel.cancelAudioRecording() }) {
                                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = { viewModel.finishAudioRecording(chatId) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PulseIndigo)
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send Audio", tint = Color.White)
                            }
                        }
                    }
                }
            } else {
                // Standard Text and Attachment Input Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { showMediaSheet = true },
                            modifier = Modifier.testTag("conversation_attachment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach File",
                                tint = PulseIndigoLight
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Message...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("conversation_input_field"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PulseIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )

                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.sendTextMessage(chatId, inputText, replyingToMessage)
                                    inputText = ""
                                    replyingToMessage = null
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PulseIndigo)
                                    .testTag("conversation_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.startAudioRecording() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PulseIndigo.copy(alpha = 0.16f))
                                    .testTag("conversation_mic_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice",
                                    tint = PulseIndigoLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Media Sheet for photos, videos, documents, location, contact
    if (showMediaSheet) {
        MediaPickerBottomSheet(
            onDismiss = { showMediaSheet = false },
            onMediaSelected = { type, caption, extra ->
                when (type) {
                    MessageType.DOCUMENT -> viewModel.sendMediaMessage(chatId, type, caption, fileName = caption, fileSize = extra)
                    MessageType.LOCATION -> viewModel.sendMediaMessage(chatId, type, caption, locationName = extra)
                    MessageType.CONTACT -> viewModel.sendMediaMessage(chatId, type, caption)
                    else -> viewModel.sendMediaMessage(chatId, type, caption, fileSize = extra)
                }
            }
        )
    }

    // Message options modal (Reply, Copy, Star, Pin, Edit, Delete)
    selectedMessageForOptions?.let { msg ->
        MessageOptionsMenu(
            message = msg,
            onDismiss = { selectedMessageForOptions = null },
            onReaction = { emoji -> viewModel.toggleReaction(msg, emoji) },
            onReply = { replyingToMessage = msg },
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Message", msg.text)
                clipboard.setPrimaryClip(clip)
            },
            onStar = { viewModel.toggleMessageStar(msg.id) },
            onPin = { viewModel.toggleMessagePin(msg.id) },
            onEdit = { editingMessage = msg },
            onDeleteForMe = { viewModel.deleteMessageForMe(msg.id) },
            onDeleteForEveryone = { viewModel.deleteMessageForEveryone(msg.id) }
        )
    }

    // Edit message dialog
    editingMessage?.let { editTarget ->
        var editText by remember { mutableStateOf(editTarget.text) }
        AlertDialog(
            onDismissRequest = { editingMessage = null },
            title = { Text("Edit Message", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editText.isNotBlank()) {
                            viewModel.editMessage(editTarget.id, editText.trim())
                            editingMessage = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMessage = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Security E2EE Key Dialog
    if (showE2EEDialog) {
        E2EEKeyDialog(
            peerName = currentChat?.name ?: "Contact",
            fingerprint = "4892 8301 7724 9912 3019 8831 4721 0023 9182 3419 7291 0021",
            onDismiss = { showE2EEDialog = false }
        )
    }
}
