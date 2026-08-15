package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerBottomSheet(
    onDismiss: () -> Unit,
    onMediaSelected: (MessageType, String, String?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Share Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MediaActionItem(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    color = Color(0xFF6366F1),
                    onClick = {
                        onMediaSelected(MessageType.IMAGE, "Shared encrypted photo", null)
                        onDismiss()
                    }
                )
                MediaActionItem(
                    icon = Icons.Default.CameraAlt,
                    label = "Camera",
                    color = Color(0xFFEC4899),
                    onClick = {
                        onMediaSelected(MessageType.IMAGE, "Photo captured with camera", null)
                        onDismiss()
                    }
                )
                MediaActionItem(
                    icon = Icons.Default.Description,
                    label = "Document",
                    color = Color(0xFF06B6D4),
                    onClick = {
                        onMediaSelected(MessageType.DOCUMENT, "Pulse_Whitepaper.pdf", "3.2 MB • PDF")
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MediaActionItem(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    color = Color(0xFF10B981),
                    onClick = {
                        onMediaSelected(MessageType.LOCATION, "Shared live location", "Market St, San Francisco")
                        onDismiss()
                    }
                )
                MediaActionItem(
                    icon = Icons.Default.Person,
                    label = "Contact",
                    color = Color(0xFFF59E0B),
                    onClick = {
                        onMediaSelected(MessageType.CONTACT, "Dr. Tariq Al-Mansoor", "+1 555-9012")
                        onDismiss()
                    }
                )
                MediaActionItem(
                    icon = Icons.Default.Videocam,
                    label = "Video",
                    color = Color(0xFF8B5CF6),
                    onClick = {
                        onMediaSelected(MessageType.VIDEO, "Screen recording demo", "12.4 MB • MP4")
                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MediaActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageOptionsMenu(
    message: Message,
    onDismiss: () -> Unit,
    onReaction: (String) -> Unit,
    onReply: () -> Unit,
    onCopy: () -> Unit,
    onStar: () -> Unit,
    onPin: () -> Unit,
    onEdit: () -> Unit,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Quick Reactions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val quickEmojis = listOf("❤️", "👍", "🔥", "😂", "😮", "😢", "🙏")
                quickEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .clickable {
                                onReaction(emoji)
                                onDismiss()
                            }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action rows
            MessageOptionRow(
                icon = Icons.Default.Reply,
                title = "Reply",
                onClick = {
                    onReply()
                    onDismiss()
                }
            )
            MessageOptionRow(
                icon = Icons.Default.ContentCopy,
                title = "Copy Text",
                onClick = {
                    onCopy()
                    onDismiss()
                }
            )
            MessageOptionRow(
                icon = if (message.isStarred) Icons.Default.StarOutline else Icons.Default.Star,
                title = if (message.isStarred) "Unstar Message" else "Star Message",
                tint = PulseAmber,
                onClick = {
                    onStar()
                    onDismiss()
                }
            )
            MessageOptionRow(
                icon = Icons.Default.PushPin,
                title = if (message.isPinned) "Unpin Message" else "Pin Message",
                onClick = {
                    onPin()
                    onDismiss()
                }
            )

            if (message.isOutgoing && message.type == MessageType.TEXT && !message.isDeleted) {
                MessageOptionRow(
                    icon = Icons.Default.Edit,
                    title = "Edit Message",
                    onClick = {
                        onEdit()
                        onDismiss()
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            MessageOptionRow(
                icon = Icons.Default.Delete,
                title = "Delete for Me",
                tint = PulseRose,
                onClick = {
                    onDeleteForMe()
                    onDismiss()
                }
            )

            if (message.isOutgoing && !message.isDeleted) {
                MessageOptionRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Delete for Everyone",
                    tint = PulseRose,
                    onClick = {
                        onDeleteForEveryone()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MessageOptionRow(
    icon: ImageVector,
    title: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CreateChatDialog(
    onDismiss: () -> Unit,
    onCreateChat: (name: String, isGroup: Boolean, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isGroup by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isGroup) "Create New Group" else "Start New Conversation",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isGroup,
                        onClick = { isGroup = false },
                        label = { Text("Direct Chat") }
                    )
                    FilterChip(
                        selected = isGroup,
                        onClick = { isGroup = true },
                        label = { Text("Group") }
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isGroup) "Group Name" else "Contact Name or Handle") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_chat_name_input")
                )

                if (isGroup) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Group Description (Optional)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreateChat(name, isGroup, if (isGroup) description else null)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo),
                modifier = Modifier.testTag("confirm_create_chat_button")
            ) {
                Text(if (isGroup) "Create Group" else "Start Chat", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
