package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    onLongClick: (Message) -> Unit,
    onReactionClick: (Message, String) -> Unit,
    onReplyClick: (Message) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutgoing = message.isOutgoing
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val bubbleColor = when {
        isOutgoing -> PulseIndigoDark
        isDark -> DarkSurfaceVariant
        else -> LightSurfaceVariant
    }

    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    val timeString = remember(message.timestamp) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .combinedClickable(
                    onClick = { /* Tap handled */ },
                    onLongClick = { onLongClick(message) }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("message_bubble_${message.id}")
        ) {
            Column {
                // Quoted Reply Banner if this message is a reply
                if (!message.replyToMessageId.isNullOrBlank()) {
                    Surface(
                        color = if (isOutgoing) PulseIndigo.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(if (isOutgoing) PulseCyanLight else PulseIndigo)
                            )
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(
                                    text = message.replyToSenderName ?: "Original Message",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOutgoing) PulseCyanLight else PulseIndigoLight
                                )
                                Text(
                                    text = message.replyToText ?: "",
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                // Message Content based on type
                when (message.type) {
                    MessageType.TEXT -> {
                        if (message.isDeleted) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "This message was deleted",
                                    fontSize = 14.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            Text(
                                text = message.text,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                    MessageType.AUDIO -> {
                        VoiceAudioPlayerBubble(
                            durationSec = message.mediaDurationSec.coerceAtLeast(8),
                            isOutgoing = isOutgoing
                        )
                    }
                    MessageType.DOCUMENT -> {
                        DocumentAttachmentCard(
                            docName = message.mediaName ?: "Document.pdf",
                            docSize = message.mediaSize ?: "2.1 MB • PDF",
                            isOutgoing = isOutgoing
                        )
                    }
                    MessageType.IMAGE -> {
                        ImageAttachmentCard(caption = message.text)
                    }
                    MessageType.VIDEO -> {
                        VideoAttachmentCard(caption = message.text)
                    }
                    MessageType.LOCATION -> {
                        LocationAttachmentCard(
                            locationName = message.locationName ?: "Shared Location"
                        )
                    }
                    MessageType.CONTACT -> {
                        ContactAttachmentCard(
                            contactName = message.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom row with Timestamp, Star, Pinned, Edited, and Delivery status
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (message.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    if (message.isStarred) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Starred",
                            tint = PulseAmber,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    if (message.isEdited) {
                        Text(
                            text = "edited",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = timeString,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    if (isOutgoing) {
                        DeliveryStatusIcon(status = message.deliveryStatus)
                    }
                }
            }
        }

        // Reactions Pills
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 2.dp, start = if (isOutgoing) 0.dp else 6.dp, end = if (isOutgoing) 6.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { reaction ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable {
                            onReactionClick(message, reaction.emoji)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = reaction.emoji, fontSize = 12.sp)
                            if (reaction.count > 1) {
                                Text(
                                    text = reaction.count.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceAudioPlayerBubble(
    durationSec: Int,
    isOutgoing: Boolean
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentSpeed by remember { mutableStateOf("1x") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Play / Pause Circle
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isOutgoing) PulseCyanLight else PulseIndigo)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isOutgoing) DarkBackground else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Voice Waveform Bars
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
        ) {
            val barsCount = 24
            val barWidth = 3.dp.toPx()
            val totalSpacing = size.width - (barsCount * barWidth)
            val gap = totalSpacing / (barsCount - 1)

            val heights = listOf(
                0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 1.0f, 0.5f, 0.8f,
                0.3f, 0.5f, 0.9f, 0.7f, 0.4f, 0.8f, 0.6f, 1.0f,
                0.4f, 0.7f, 0.3f, 0.6f, 0.8f, 0.5f, 0.4f, 0.2f
            )

            for (i in 0 until barsCount) {
                val hFactor = heights.getOrElse(i) { 0.5f }
                val barHeight = size.height * hFactor
                val x = i * (barWidth + gap)
                val y = (size.height - barHeight) / 2f
                val color = if (i < 10) (if (isOutgoing) PulseCyanLight else PulseIndigoLight) else Color.White.copy(alpha = 0.4f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }
        }

        // Speed Toggle Button
        Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable {
                currentSpeed = when (currentSpeed) {
                    "1x" -> "1.5x"
                    "1.5x" -> "2x"
                    else -> "1x"
                }
            }
        ) {
            Text(
                text = currentSpeed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun DocumentAttachmentCard(
    docName: String,
    docSize: String,
    isOutgoing: Boolean
) {
    Surface(
        color = Color.White.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isOutgoing) PulseCyanLight else PulseIndigo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isOutgoing) DarkBackground else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = docName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = docSize,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ImageAttachmentCard(caption: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1E1B4B), Color(0xFF0E7490), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "High-Res Encrypted Photo",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        if (caption.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = caption,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun VideoAttachmentCard(caption: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF311042), Color(0xFF4C1D95), Color(0xFF1E1B4B))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "0:45",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
        if (caption.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = caption,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun LocationAttachmentCard(locationName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF059669), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = PulseRose,
                modifier = Modifier.size(32.dp)
            )
        }
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = PulseCyanLight,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = locationName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Live GPS Location • Tap to view map",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ContactAttachmentCard(contactName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PulseIndigoLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contactName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
            Text(
                text = "PulseChat Contact Card",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = PulseCyanDark),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Message", fontSize = 11.sp, color = Color.White)
        }
    }
}
