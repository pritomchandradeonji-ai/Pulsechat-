package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ChatType
import com.example.data.model.DeliveryStatus
import com.example.data.model.MessageType
import com.example.data.model.MessageReaction
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // DIRECT, GROUP
    val avatarUrl: String?,
    val avatarColorHex: Long,
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isMuted: Boolean,
    val isArchived: Boolean,
    val isOnline: Boolean,
    val isTyping: Boolean,
    val isRecording: Boolean,
    val disappearingMessageTimeSec: Long,
    val groupMemberCount: Int,
    val groupDescription: String?,
    val wallpaperTheme: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: String, // TEXT, IMAGE, etc.
    val mediaUrl: String?,
    val mediaName: String?,
    val mediaSize: String?,
    val mediaDurationSec: Int,
    val timestamp: Long,
    val deliveryStatus: String,
    val isOutgoing: Boolean,
    val replyToMessageId: String?,
    val replyToSenderName: String?,
    val replyToText: String?,
    val isStarred: Boolean,
    val isPinned: Boolean,
    val isEdited: Boolean,
    val isDeleted: Boolean,
    val reactionsJson: String, // serialized JSON of List<MessageReaction>
    val latitude: Double?,
    val longitude: Double?,
    val locationName: String?
)

@Entity(tableName = "status_stories")
data class StatusStoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userAvatarColorHex: Long,
    val type: String,
    val content: String,
    val backgroundGradientHex: Long,
    val timestamp: Long,
    val expiresAt: Long,
    val isMyStatus: Boolean,
    val viewersCount: Int
)

@Entity(tableName = "call_records")
data class CallRecordEntity(
    @PrimaryKey val id: String,
    val peerName: String,
    val peerAvatarColorHex: Long,
    val callType: String,
    val direction: String,
    val timestamp: Long,
    val durationSec: Int,
    val wasMissed: Boolean
)
