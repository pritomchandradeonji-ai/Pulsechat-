package com.example.data.model

import java.util.UUID

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, LOCATION, CONTACT
}

enum class DeliveryStatus {
    SENDING, SENT, DELIVERED, READ
}

enum class ChatType {
    DIRECT, GROUP
}

enum class CallType {
    VOICE, VIDEO
}

enum class CallDirection {
    INCOMING, OUTGOING, MISSED
}

enum class CallState {
    IDLE, RINGING, CONNECTING, CONNECTED, ENDED
}

enum class StoryType {
    TEXT, IMAGE, VIDEO, VOICE
}

enum class PrivacyLevel {
    EVERYONE, MY_CONTACTS, NOBODY
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val username: String,
    val phone: String,
    val email: String,
    val bio: String = "Available for chats on PulseChat",
    val avatarUrl: String? = null,
    val avatarColorHex: Long = 0xFF6366F1,
    val isOnline: Boolean = false,
    val lastSeen: String = "Just now",
    val isVerified: Boolean = false,
    val publicKeyFingerprint: String = "4892 8301 7724 9912 3019 8831 4721 0023 9182 3419 7291 0021"
)

data class MessageReaction(
    val emoji: String,
    val userId: String,
    val count: Int = 1
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaName: String? = null,
    val mediaSize: String? = null,
    val mediaDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val deliveryStatus: DeliveryStatus = DeliveryStatus.READ,
    val isOutgoing: Boolean = false,
    val replyToMessageId: String? = null,
    val replyToSenderName: String? = null,
    val replyToText: String? = null,
    val isStarred: Boolean = false,
    val isPinned: Boolean = false,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val reactions: List<MessageReaction> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null
)

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ChatType = ChatType.DIRECT,
    val avatarUrl: String? = null,
    val avatarColorHex: Long = 0xFF6366F1,
    val lastMessage: String = "",
    val lastMessageTime: String = "12:00 PM",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val isRecording: Boolean = false,
    val disappearingMessageTimeSec: Long = 0, // 0 = off, 86400 = 24h, etc.
    val groupMemberCount: Int = 0,
    val groupDescription: String? = null,
    val adminIds: List<String> = emptyList(),
    val wallpaperTheme: String = "Default"
)

data class StatusStory(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val userAvatarColorHex: Long = 0xFF6366F1,
    val type: StoryType = StoryType.TEXT,
    val content: String, // Text, image placeholder, or audio waveform descriptor
    val backgroundGradientHex: Long = 0xFF1E1B4B,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L,
    val isMyStatus: Boolean = false,
    val viewersCount: Int = 0,
    val viewers: List<String> = emptyList()
)

data class CommunityGroup(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val memberCount: Int,
    val isAnnouncementOnly: Boolean = false
)

data class Community(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val avatarColorHex: Long = 0xFF06B6D4,
    val memberCount: Int = 120,
    val isMember: Boolean = true,
    val groups: List<CommunityGroup> = emptyList()
)

data class PollOption(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val votes: Int = 0,
    val hasVoted: Boolean = false
)

data class ChannelPost(
    val id: String = UUID.randomUUID().toString(),
    val channelId: String,
    val title: String? = null,
    val content: String,
    val mediaUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val viewsCount: Int = 342,
    val reactionsCount: Map<String, Int> = mapOf("❤️" to 45, "🔥" to 88, "👏" to 22),
    val pollQuestion: String? = null,
    val pollOptions: List<PollOption> = emptyList(),
    val commentsCount: Int = 14
)

data class Channel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val handle: String,
    val description: String,
    val avatarColorHex: Long = 0xFF10B981,
    val subscriberCount: Int = 12500,
    val isSubscribed: Boolean = true,
    val isVerified: Boolean = true,
    val recentPosts: List<ChannelPost> = emptyList()
)

data class CallRecord(
    val id: String = UUID.randomUUID().toString(),
    val peerName: String,
    val peerAvatarColorHex: Long = 0xFF6366F1,
    val callType: CallType = CallType.VOICE,
    val direction: CallDirection = CallDirection.INCOMING,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSec: Int = 0,
    val wasMissed: Boolean = false
)

data class DeviceSession(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val platform: String, // Android, Web Chrome, macOS, iPadOS
    val location: String,
    val lastActive: String,
    val isCurrentDevice: Boolean = false
)

data class PrivacySettings(
    val lastSeenPrivacy: PrivacyLevel = PrivacyLevel.MY_CONTACTS,
    val profilePhotoPrivacy: PrivacyLevel = PrivacyLevel.EVERYONE,
    val aboutPrivacy: PrivacyLevel = PrivacyLevel.EVERYONE,
    val statusPrivacy: PrivacyLevel = PrivacyLevel.MY_CONTACTS,
    val readReceiptsEnabled: Boolean = true,
    val onlineStatusPrivacy: PrivacyLevel = PrivacyLevel.EVERYONE,
    val defaultDisappearingTimer: String = "Off"
)

data class SecuritySettings(
    val twoStepVerificationEnabled: Boolean = false,
    val twoStepPin: String = "",
    val recoveryEmail: String = "",
    val biometricLockEnabled: Boolean = false,
    val showSecurityNotifications: Boolean = true
)

data class AdminSystemStats(
    val activeUsersCount: Int = 142850,
    val messagesPerSecond: Int = 4280,
    val activeCallsCount: Int = 890,
    val storageUsedGb: Double = 842.6,
    val serverLatencyMs: Int = 18,
    val spamReportsPending: Int = 7,
    val uptimePercentage: Double = 99.98
)
