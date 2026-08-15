package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.CallRecordEntity
import com.example.data.local.ChatEntity
import com.example.data.local.MessageEntity
import com.example.data.local.StatusStoryEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PulseRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val chatDao = db.chatDao()
    private val messageDao = db.messageDao()
    private val statusDao = db.statusDao()
    private val callDao = db.callDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current User Profile State
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            name = "Alex Vance",
            username = "alex_vance",
            phone = "+1 (555) 019-2834",
            email = "alex.vance@pulse.io",
            bio = "Building the future of decentralized real-time networks ⚡️",
            avatarColorHex = 0xFF6366F1,
            isOnline = true,
            isVerified = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Configurable App Name (Admin controlled)
    private val _appName = MutableStateFlow("PulseChat")
    val appName: StateFlow<String> = _appName.asStateFlow()

    // Active App Language (English, Bengali, Arabic)
    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Privacy & Security Settings
    private val _privacySettings = MutableStateFlow(PrivacySettings())
    val privacySettings: StateFlow<PrivacySettings> = _privacySettings.asStateFlow()

    private val _securitySettings = MutableStateFlow(SecuritySettings())
    val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

    // Linked Devices State
    private val _linkedDevices = MutableStateFlow(
        listOf(
            DeviceSession(
                id = "dev_1",
                deviceName = "Pixel 8 Pro (This Device)",
                platform = "Android 15",
                location = "San Francisco, USA",
                lastActive = "Active now",
                isCurrentDevice = true
            ),
            DeviceSession(
                id = "dev_2",
                deviceName = "Pulse Web - Chrome",
                platform = "macOS Sequoia",
                location = "San Francisco, USA",
                lastActive = "10 minutes ago",
                isCurrentDevice = false
            ),
            DeviceSession(
                id = "dev_3",
                deviceName = "Pulse Desktop Studio",
                platform = "Windows 11",
                location = "London, UK",
                lastActive = "Yesterday at 4:20 PM",
                isCurrentDevice = false
            )
        )
    )
    val linkedDevices: StateFlow<List<DeviceSession>> = _linkedDevices.asStateFlow()

    // Blocked Users State
    private val _blockedUsers = MutableStateFlow<List<String>>(emptyList())
    val blockedUsers: StateFlow<List<String>> = _blockedUsers.asStateFlow()

    // Channels State
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    // Communities State
    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Admin System Stats
    private val _adminStats = MutableStateFlow(AdminSystemStats())
    val adminStats: StateFlow<AdminSystemStats> = _adminStats.asStateFlow()

    // Active Call State
    private val _activeCall = MutableStateFlow<ActiveCallSession?>(null)
    val activeCall: StateFlow<ActiveCallSession?> = _activeCall.asStateFlow()

    data class ActiveCallSession(
        val callId: String = UUID.randomUUID().toString(),
        val peerName: String,
        val peerAvatarColor: Long,
        val callType: CallType,
        val durationSeconds: Int = 0,
        val isMuted: Boolean = false,
        val isSpeakerOn: Boolean = true,
        val isVideoEnabled: Boolean = true,
        val isFrontCamera: Boolean = true,
        val networkQuality: String = "Excellent (18ms HD)",
        val isConnected: Boolean = true
    )

    init {
        scope.launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val initialChats = listOf(
            ChatEntity(
                id = "chat_1",
                name = "Elena Rostova",
                type = ChatType.DIRECT.name,
                avatarUrl = null,
                avatarColorHex = 0xFF06B6D4,
                lastMessage = "Sent the updated E2EE key architecture specs! 🔒",
                lastMessageTime = "11:42 AM",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                unreadCount = 2,
                isPinned = true,
                isMuted = false,
                isArchived = false,
                isOnline = true,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 86400,
                groupMemberCount = 0,
                groupDescription = null,
                wallpaperTheme = "Obsidian Glow"
            ),
            ChatEntity(
                id = "chat_2",
                name = "Pulse Core Engineering",
                type = ChatType.GROUP.name,
                avatarUrl = null,
                avatarColorHex = 0xFF6366F1,
                lastMessage = "Marcus: Release 3.4 APK compiled with zero regressions.",
                lastMessageTime = "10:15 AM",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                unreadCount = 0,
                isPinned = true,
                isMuted = false,
                isArchived = false,
                isOnline = true,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 0,
                groupMemberCount = 48,
                groupDescription = "Official engine developers & contributors",
                wallpaperTheme = "Matrix Dark"
            ),
            ChatEntity(
                id = "chat_3",
                name = "Dr. Tariq Al-Mansoor",
                type = ChatType.DIRECT.name,
                avatarUrl = null,
                avatarColorHex = 0xFF10B981,
                lastMessage = "🎙 Voice note (0:24)",
                lastMessageTime = "Yesterday",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 20,
                unreadCount = 0,
                isPinned = false,
                isMuted = false,
                isArchived = false,
                isOnline = false,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 0,
                groupMemberCount = 0,
                groupDescription = null,
                wallpaperTheme = "Default"
            ),
            ChatEntity(
                id = "chat_4",
                name = "Amina Kabir",
                type = ChatType.DIRECT.name,
                avatarUrl = null,
                avatarColorHex = 0xFFF43F5E,
                lastMessage = "Let's review the new WebRTC audio codec benchmarks.",
                lastMessageTime = "Yesterday",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 26,
                unreadCount = 0,
                isPinned = false,
                isMuted = false,
                isArchived = false,
                isOnline = true,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 0,
                groupMemberCount = 0,
                groupDescription = null,
                wallpaperTheme = "Default"
            ),
            ChatEntity(
                id = "chat_5",
                name = "Global Web3 & AI Builders",
                type = ChatType.GROUP.name,
                avatarUrl = null,
                avatarColorHex = 0xFFF59E0B,
                lastMessage = "Liam: Anyone joining the keynote live stream?",
                lastMessageTime = "Aug 12",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
                unreadCount = 0,
                isPinned = false,
                isMuted = true,
                isArchived = false,
                isOnline = false,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 0,
                groupMemberCount = 3120,
                groupDescription = "Worldwide decentralized communications ecosystem",
                wallpaperTheme = "Default"
            )
        )
        chatDao.insertChats(initialChats)

        // Seed initial messages for Elena Rostova
        val elenaMessages = listOf(
            MessageEntity(
                id = "msg_1",
                chatId = "chat_1",
                senderId = "chat_1",
                senderName = "Elena Rostova",
                text = "Hey Alex! Did you check the latest zero-knowledge protocol upgrade?",
                type = MessageType.TEXT.name,
                mediaUrl = null,
                mediaName = null,
                mediaSize = null,
                mediaDurationSec = 0,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = false,
                replyToMessageId = null,
                replyToSenderName = null,
                replyToText = null,
                isStarred = false,
                isPinned = true,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[]",
                latitude = null,
                longitude = null,
                locationName = null
            ),
            MessageEntity(
                id = "msg_2",
                chatId = "chat_1",
                senderId = "user_me",
                senderName = "Alex Vance",
                text = "Yes! The cryptographic handshake reduces round-trip latency to under 20ms.",
                type = MessageType.TEXT.name,
                mediaUrl = null,
                mediaName = null,
                mediaSize = null,
                mediaDurationSec = 0,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = true,
                replyToMessageId = "msg_1",
                replyToSenderName = "Elena Rostova",
                replyToText = "Hey Alex! Did you check the latest zero-knowledge protocol upgrade?",
                isStarred = true,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[{\"emoji\":\"🚀\",\"userId\":\"chat_1\",\"count\":1}]",
                latitude = null,
                longitude = null,
                locationName = null
            ),
            MessageEntity(
                id = "msg_3",
                chatId = "chat_1",
                senderId = "chat_1",
                senderName = "Elena Rostova",
                text = "Audio briefing on the distributed cluster architecture",
                type = MessageType.AUDIO.name,
                mediaUrl = "sample_audio.aac",
                mediaName = "Voice_Note_0814.aac",
                mediaSize = "1.2 MB",
                mediaDurationSec = 28,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = false,
                replyToMessageId = null,
                replyToSenderName = null,
                replyToText = null,
                isStarred = false,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[{\"emoji\":\"❤️\",\"userId\":\"user_me\",\"count\":1}]",
                latitude = null,
                longitude = null,
                locationName = null
            ),
            MessageEntity(
                id = "msg_4",
                chatId = "chat_1",
                senderId = "chat_1",
                senderName = "Elena Rostova",
                text = "Pulse_Architecture_v2.pdf",
                type = MessageType.DOCUMENT.name,
                mediaUrl = "sample_doc.pdf",
                mediaName = "Pulse_Architecture_v2.pdf",
                mediaSize = "4.8 MB • PDF Document",
                mediaDurationSec = 0,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = false,
                replyToMessageId = null,
                replyToSenderName = null,
                replyToText = null,
                isStarred = true,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[]",
                latitude = null,
                longitude = null,
                locationName = null
            ),
            MessageEntity(
                id = "msg_5",
                chatId = "chat_1",
                senderId = "chat_1",
                senderName = "Elena Rostova",
                text = "Sent the updated E2EE key architecture specs! 🔒",
                type = MessageType.TEXT.name,
                mediaUrl = null,
                mediaName = null,
                mediaSize = null,
                mediaDurationSec = 0,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = false,
                replyToMessageId = null,
                replyToSenderName = null,
                replyToText = null,
                isStarred = false,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[]",
                latitude = null,
                longitude = null,
                locationName = null
            )
        )
        messageDao.insertMessages(elenaMessages)

        // Seed initial Statuses
        val initialStatuses = listOf(
            StatusStoryEntity(
                id = "status_1",
                userId = "user_me",
                userName = "My Status",
                userAvatarColorHex = 0xFF6366F1,
                type = StoryType.TEXT.name,
                content = "Deploying PulseChat v3.4 globally 🌐✨ Fast, private, and encrypted!",
                backgroundGradientHex = 0xFF4338CA,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                expiresAt = System.currentTimeMillis() + 86400000L,
                isMyStatus = true,
                viewersCount = 38
            ),
            StatusStoryEntity(
                id = "status_2",
                userId = "chat_1",
                userName = "Elena Rostova",
                userAvatarColorHex = 0xFF06B6D4,
                type = StoryType.TEXT.name,
                content = "Keynote at Distributed Tech Summit today! 🎙⚡️",
                backgroundGradientHex = 0xFF0E7490,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                expiresAt = System.currentTimeMillis() + 86400000L,
                isMyStatus = false,
                viewersCount = 142
            ),
            StatusStoryEntity(
                id = "status_3",
                userId = "chat_4",
                userName = "Amina Kabir",
                userAvatarColorHex = 0xFFF43F5E,
                type = StoryType.TEXT.name,
                content = "Enjoying the sunset after an intense hackathon session 🌅",
                backgroundGradientHex = 0xFF9F1239,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 240,
                expiresAt = System.currentTimeMillis() + 86400000L,
                isMyStatus = false,
                viewersCount = 65
            )
        )
        statusDao.insertStatuses(initialStatuses)

        // Seed initial Calls
        val initialCalls = listOf(
            CallRecordEntity(
                id = "call_1",
                peerName = "Elena Rostova",
                peerAvatarColorHex = 0xFF06B6D4,
                callType = CallType.VIDEO.name,
                direction = CallDirection.INCOMING.name,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                durationSec = 542,
                wasMissed = false
            ),
            CallRecordEntity(
                id = "call_2",
                peerName = "Dr. Tariq Al-Mansoor",
                peerAvatarColorHex = 0xFF10B981,
                callType = CallType.VOICE.name,
                direction = CallDirection.MISSED.name,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 18,
                durationSec = 0,
                wasMissed = true
            ),
            CallRecordEntity(
                id = "call_3",
                peerName = "Pulse Core Engineering (Group Call)",
                peerAvatarColorHex = 0xFF6366F1,
                callType = CallType.VOICE.name,
                direction = CallDirection.OUTGOING.name,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 42,
                durationSec = 1420,
                wasMissed = false
            )
        )
        callDao.insertCalls(initialCalls)

        // Seed Channels
        _channels.value = listOf(
            Channel(
                id = "chan_1",
                name = "Pulse Tech & AI Pulse",
                handle = "@pulse_tech",
                description = "Official updates on cryptography, mobile edge-computing, and real-time P2P systems.",
                avatarColorHex = 0xFF6366F1,
                subscriberCount = 24580,
                isSubscribed = true,
                recentPosts = listOf(
                    ChannelPost(
                        id = "post_1",
                        channelId = "chan_1",
                        title = "🚀 Pulse Protocol 2.0 Live",
                        content = "We have officially enabled real-time multi-device end-to-end ratchet synchronization. Message delivery latency is now down to 14ms across all global cloud hubs.",
                        viewsCount = 14820,
                        reactionsCount = mapOf("🔥" to 640, "❤️" to 890, "👏" to 310),
                        pollQuestion = "Which protocol feature do you utilize the most?",
                        pollOptions = listOf(
                            PollOption("opt_1", "End-to-End Encryption Keys", 420, false),
                            PollOption("opt_2", "HD Audio/Video WebRTC Calls", 812, true),
                            PollOption("opt_3", "Ephemeral 24h Disappearing Notes", 290, false)
                        ),
                        commentsCount = 84
                    ),
                    ChannelPost(
                        id = "post_2",
                        channelId = "chan_1",
                        title = "🔒 Security Audit Report Published",
                        content = "Independent cryptanalysis confirms zero zero-day vulnerabilities in our ratcheting key derivation structure. Download the full paper in settings.",
                        viewsCount = 9430,
                        reactionsCount = mapOf("🛡️" to 520, "👍" to 310),
                        commentsCount = 37
                    )
                )
            ),
            Channel(
                id = "chan_2",
                name = "Daily Global Insights",
                handle = "@global_insights",
                description = "Curated scientific discoveries, tech breakthroughs, and creative inspirations.",
                avatarColorHex = 0xFF06B6D4,
                subscriberCount = 18900,
                isSubscribed = true,
                recentPosts = listOf(
                    ChannelPost(
                        id = "post_3",
                        channelId = "chan_2",
                        title = "🌌 Next-Gen Quantum Sensors",
                        content = "Researchers have demonstrated diamond nitrogen-vacancy quantum sensors capable of measuring micro-magnetic field oscillations with unprecedented fidelity.",
                        viewsCount = 8200,
                        reactionsCount = mapOf("💡" to 310, "✨" to 420),
                        commentsCount = 19
                    )
                )
            )
        )

        // Seed Communities
        _communities.value = listOf(
            Community(
                id = "comm_1",
                name = "Global Software Architects",
                description = "A space for distributed systems engineers, protocol researchers, and system designers.",
                avatarColorHex = 0xFF6366F1,
                memberCount = 840,
                isMember = true,
                groups = listOf(
                    CommunityGroup("cgroup_1", "📢 Community Announcements", "Official updates from organizers", 840, true),
                    CommunityGroup("cgroup_2", "⚡️ Real-time WebRTC & Audio", "Discussions on opus codecs & peer mesh", 320, false),
                    CommunityGroup("cgroup_3", "🛡️ Cryptography & E2EE Ratchets", "Key exchange, PFS, and zero knowledge", 410, false)
                )
            ),
            Community(
                id = "comm_2",
                name = "Mobile & Jetpack Creators Guild",
                description = "Modern Android and cross-platform UI/UX designers crafting next-gen experiences.",
                avatarColorHex = 0xFF10B981,
                memberCount = 1250,
                isMember = true,
                groups = listOf(
                    CommunityGroup("cgroup_4", "📢 Showcase & Releases", "Member creations and beta launches", 1250, true),
                    CommunityGroup("cgroup_5", "🎨 Design Systems & M3 Tokens", "Animations, typography, and glassmorphism", 680, false)
                )
            )
        )
    }

    // Reactive DAO streams
    fun getActiveChats(): Flow<List<Chat>> = chatDao.getActiveChats().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getArchivedChats(): Flow<List<Chat>> = chatDao.getArchivedChats().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getChatById(chatId: String): Flow<Chat?> = chatDao.getChatById(chatId).map { it?.toDomain() }

    fun getMessagesForChat(chatId: String): Flow<List<Message>> = messageDao.getMessagesForChat(chatId).map { entities ->
        entities.map { it.toDomain() }
    }

    fun getStarredMessages(): Flow<List<Message>> = messageDao.getStarredMessages().map { entities ->
        entities.map { it.toDomain() }
    }

    fun searchMessages(query: String): Flow<List<Message>> = messageDao.searchMessages(query).map { entities ->
        entities.map { it.toDomain() }
    }

    fun getAllStatuses(): Flow<List<StatusStory>> = statusDao.getAllStatuses().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getAllCalls(): Flow<List<CallRecord>> = callDao.getAllCalls().map { entities ->
        entities.map { it.toDomain() }
    }

    // Message Actions
    fun sendMessage(
        chatId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        mediaName: String? = null,
        mediaSize: String? = null,
        mediaDurationSec: Int = 0,
        replyToMessage: Message? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        locationName: String? = null
    ) {
        scope.launch {
            val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val messageId = "msg_" + UUID.randomUUID().toString().take(8)

            val newMsg = MessageEntity(
                id = messageId,
                chatId = chatId,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.name,
                text = text,
                type = type.name,
                mediaUrl = mediaUrl,
                mediaName = mediaName,
                mediaSize = mediaSize,
                mediaDurationSec = mediaDurationSec,
                timestamp = System.currentTimeMillis(),
                deliveryStatus = DeliveryStatus.SENT.name,
                isOutgoing = true,
                replyToMessageId = replyToMessage?.id,
                replyToSenderName = replyToMessage?.senderName,
                replyToText = replyToMessage?.text,
                isStarred = false,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[]",
                latitude = latitude,
                longitude = longitude,
                locationName = locationName
            )
            messageDao.insertMessage(newMsg)

            val previewText = when (type) {
                MessageType.TEXT -> text
                MessageType.AUDIO -> "🎙 Voice note (${mediaDurationSec}s)"
                MessageType.IMAGE -> "📷 Photo"
                MessageType.VIDEO -> "📹 Video"
                MessageType.DOCUMENT -> "📄 $mediaName"
                MessageType.LOCATION -> "📍 Location: $locationName"
                MessageType.CONTACT -> "👤 Contact card"
            }
            chatDao.updateLastMessage(chatId, previewText, timeString, System.currentTimeMillis())

            // Simulate intelligent counterpart automatic reply with typing / recording indicator
            delay(1200)
            val replyText = generateAutoReply(text, type)
            val incomingId = "msg_in_" + UUID.randomUUID().toString().take(8)
            val incomingMsg = MessageEntity(
                id = incomingId,
                chatId = chatId,
                senderId = chatId,
                senderName = "Contact",
                text = replyText,
                type = MessageType.TEXT.name,
                mediaUrl = null,
                mediaName = null,
                mediaSize = null,
                mediaDurationSec = 0,
                timestamp = System.currentTimeMillis(),
                deliveryStatus = DeliveryStatus.READ.name,
                isOutgoing = false,
                replyToMessageId = null,
                replyToSenderName = null,
                replyToText = null,
                isStarred = false,
                isPinned = false,
                isEdited = false,
                isDeleted = false,
                reactionsJson = "[]",
                latitude = null,
                longitude = null,
                locationName = null
            )
            messageDao.insertMessage(incomingMsg)
            chatDao.updateLastMessage(chatId, replyText, SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()), System.currentTimeMillis())
        }
    }

    private fun generateAutoReply(userText: String, type: MessageType): String {
        return when {
            type == MessageType.AUDIO -> "Got your voice message! Loud and crystal clear on the opus codec. 👍"
            type == MessageType.IMAGE -> "Great shot! The resolution looks super crisp."
            type == MessageType.DOCUMENT -> "Received the document. Verifying checksum now."
            type == MessageType.LOCATION -> "Thanks for sharing your live location. See you shortly!"
            userText.contains("hello", ignoreCase = true) || userText.contains("hi", ignoreCase = true) ->
                "Hello! Great to connect with you on PulseChat. Everything is secured with our zero-knowledge protocol."
            userText.contains("call", ignoreCase = true) ->
                "Sure! Feel free to start a voice or HD video call anytime using the top buttons."
            userText.contains("security", ignoreCase = true) || userText.contains("encryption", ignoreCase = true) ->
                "Our end-to-end encryption uses Double Ratchet + Curve25519 for perfect forward secrecy!"
            else -> "Got your message: \"${userText.take(30)}...\". Synced across all connected sessions."
        }
    }

    fun editMessage(messageId: String, newText: String) {
        scope.launch {
            messageDao.editMessage(messageId, newText)
        }
    }

    fun toggleMessageStar(messageId: String) {
        scope.launch {
            messageDao.toggleStar(messageId)
        }
    }

    fun toggleMessagePin(messageId: String) {
        scope.launch {
            messageDao.togglePin(messageId)
        }
    }

    fun toggleReaction(message: Message, emoji: String) {
        scope.launch {
            val currentReactions = message.reactions.toMutableList()
            val existing = currentReactions.find { it.emoji == emoji && it.userId == _currentUser.value.id }
            if (existing != null) {
                currentReactions.remove(existing)
            } else {
                currentReactions.add(MessageReaction(emoji = emoji, userId = _currentUser.value.id, count = 1))
            }
            val json = serializeReactions(currentReactions)
            messageDao.updateReactions(message.id, json)
        }
    }

    fun deleteMessageForMe(messageId: String) {
        scope.launch {
            messageDao.deleteMessage(messageId)
        }
    }

    fun deleteMessageForEveryone(messageId: String) {
        scope.launch {
            messageDao.markMessageDeletedForEveryone(messageId)
        }
    }

    fun toggleChatPin(chatId: String) {
        scope.launch {
            chatDao.togglePin(chatId)
        }
    }

    fun toggleChatMute(chatId: String) {
        scope.launch {
            chatDao.toggleMute(chatId)
        }
    }

    fun toggleChatArchive(chatId: String) {
        scope.launch {
            chatDao.toggleArchive(chatId)
        }
    }

    fun markChatAsRead(chatId: String) {
        scope.launch {
            chatDao.markChatAsRead(chatId)
        }
    }

    fun createNewChat(name: String, isGroup: Boolean, description: String? = null) {
        scope.launch {
            val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val newChat = ChatEntity(
                id = "chat_" + UUID.randomUUID().toString().take(8),
                name = name,
                type = if (isGroup) ChatType.GROUP.name else ChatType.DIRECT.name,
                avatarUrl = null,
                avatarColorHex = if (isGroup) 0xFF6366F1 else 0xFF06B6D4,
                lastMessage = if (isGroup) "Group created" else "Chat started",
                lastMessageTime = timeString,
                lastMessageTimestamp = System.currentTimeMillis(),
                unreadCount = 0,
                isPinned = false,
                isMuted = false,
                isArchived = false,
                isOnline = true,
                isTyping = false,
                isRecording = false,
                disappearingMessageTimeSec = 0,
                groupMemberCount = if (isGroup) 4 else 0,
                groupDescription = description,
                wallpaperTheme = "Default"
            )
            chatDao.insertChat(newChat)
        }
    }

    fun postStatusStory(content: String, type: StoryType, gradientHex: Long) {
        scope.launch {
            val story = StatusStoryEntity(
                id = "status_" + UUID.randomUUID().toString().take(8),
                userId = _currentUser.value.id,
                userName = "My Status",
                userAvatarColorHex = _currentUser.value.avatarColorHex,
                type = type.name,
                content = content,
                backgroundGradientHex = gradientHex,
                timestamp = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + 86400000L,
                isMyStatus = true,
                viewersCount = 0
            )
            statusDao.insertStatus(story)
        }
    }

    fun startCall(peerName: String, peerAvatarColor: Long, callType: CallType) {
        _activeCall.value = ActiveCallSession(
            peerName = peerName,
            peerAvatarColor = peerAvatarColor,
            callType = callType,
            durationSeconds = 0,
            isConnected = true
        )
        scope.launch {
            val newCall = CallRecordEntity(
                id = "call_" + UUID.randomUUID().toString().take(8),
                peerName = peerName,
                peerAvatarColorHex = peerAvatarColor,
                callType = callType.name,
                direction = CallDirection.OUTGOING.name,
                timestamp = System.currentTimeMillis(),
                durationSec = 0,
                wasMissed = false
            )
            callDao.insertCall(newCall)
        }
    }

    fun updateActiveCall(update: (ActiveCallSession) -> ActiveCallSession) {
        _activeCall.value?.let {
            _activeCall.value = update(it)
        }
    }

    fun endActiveCall() {
        _activeCall.value = null
    }

    fun votePoll(channelId: String, postId: String, optionId: String) {
        val updated = _channels.value.map { channel ->
            if (channel.id == channelId) {
                val newPosts = channel.recentPosts.map { post ->
                    if (post.id == postId) {
                        val newOptions = post.pollOptions.map { opt ->
                            if (opt.id == optionId) {
                                opt.copy(votes = opt.votes + 1, hasVoted = true)
                            } else {
                                opt
                            }
                        }
                        post.copy(pollOptions = newOptions)
                    } else post
                }
                channel.copy(recentPosts = newPosts)
            } else channel
        }
        _channels.value = updated
    }

    fun toggleChannelSubscription(channelId: String) {
        _channels.value = _channels.value.map {
            if (it.id == channelId) it.copy(
                isSubscribed = !it.isSubscribed,
                subscriberCount = if (it.isSubscribed) it.subscriberCount - 1 else it.subscriberCount + 1
            ) else it
        }
    }

    fun createChannel(name: String, handle: String, description: String) {
        val newChan = Channel(
            id = "chan_" + UUID.randomUUID().toString().take(8),
            name = name,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            description = description,
            avatarColorHex = 0xFF10B981,
            subscriberCount = 1,
            isSubscribed = true,
            isVerified = false,
            recentPosts = emptyList()
        )
        _channels.value = listOf(newChan) + _channels.value
    }

    fun createCommunity(name: String, description: String) {
        val newComm = Community(
            id = "comm_" + UUID.randomUUID().toString().take(8),
            name = name,
            description = description,
            avatarColorHex = 0xFF6366F1,
            memberCount = 1,
            isMember = true,
            groups = listOf(
                CommunityGroup("cg_" + UUID.randomUUID().toString().take(6), "📢 Announcements", "Official updates", 1, true),
                CommunityGroup("cg_" + UUID.randomUUID().toString().take(6), "General Discussions", "Open lounge", 1, false)
            )
        )
        _communities.value = listOf(newComm) + _communities.value
    }

    fun updateProfile(name: String, username: String, bio: String, phone: String, email: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            username = username,
            bio = bio,
            phone = phone,
            email = email
        )
    }

    fun updateAppName(newName: String) {
        if (newName.isNotBlank()) {
            _appName.value = newName.trim()
        }
    }

    fun setAppLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun updatePrivacySettings(update: (PrivacySettings) -> PrivacySettings) {
        _privacySettings.value = update(_privacySettings.value)
    }

    fun updateSecuritySettings(update: (SecuritySettings) -> SecuritySettings) {
        _securitySettings.value = update(_securitySettings.value)
    }

    fun terminateDeviceSession(sessionId: String) {
        _linkedDevices.value = _linkedDevices.value.filter { it.id != sessionId }
    }

    fun blockUser(userId: String) {
        if (!_blockedUsers.value.contains(userId)) {
            _blockedUsers.value = _blockedUsers.value + userId
        }
    }

    fun unblockUser(userId: String) {
        _blockedUsers.value = _blockedUsers.value - userId
    }

    // Helper converters
    private fun ChatEntity.toDomain(): Chat = Chat(
        id = id,
        name = name,
        type = try { ChatType.valueOf(type) } catch (e: Exception) { ChatType.DIRECT },
        avatarUrl = avatarUrl,
        avatarColorHex = avatarColorHex,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount,
        isPinned = isPinned,
        isMuted = isMuted,
        isArchived = isArchived,
        isOnline = isOnline,
        isTyping = isTyping,
        isRecording = isRecording,
        disappearingMessageTimeSec = disappearingMessageTimeSec,
        groupMemberCount = groupMemberCount,
        groupDescription = groupDescription,
        wallpaperTheme = wallpaperTheme
    )

    private fun MessageEntity.toDomain(): Message = Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        text = text,
        type = try { MessageType.valueOf(type) } catch (e: Exception) { MessageType.TEXT },
        mediaUrl = mediaUrl,
        mediaName = mediaName,
        mediaSize = mediaSize,
        mediaDurationSec = mediaDurationSec,
        timestamp = timestamp,
        deliveryStatus = try { DeliveryStatus.valueOf(deliveryStatus) } catch (e: Exception) { DeliveryStatus.READ },
        isOutgoing = isOutgoing,
        replyToMessageId = replyToMessageId,
        replyToSenderName = replyToSenderName,
        replyToText = replyToText,
        isStarred = isStarred,
        isPinned = isPinned,
        isEdited = isEdited,
        isDeleted = isDeleted,
        reactions = parseReactions(reactionsJson),
        latitude = latitude,
        longitude = longitude,
        locationName = locationName
    )

    private fun StatusStoryEntity.toDomain(): StatusStory = StatusStory(
        id = id,
        userId = userId,
        userName = userName,
        userAvatarColorHex = userAvatarColorHex,
        type = try { StoryType.valueOf(type) } catch (e: Exception) { StoryType.TEXT },
        content = content,
        backgroundGradientHex = backgroundGradientHex,
        timestamp = timestamp,
        expiresAt = expiresAt,
        isMyStatus = isMyStatus,
        viewersCount = viewersCount,
        viewers = listOf("Elena Rostova", "Dr. Tariq Al-Mansoor", "Amina Kabir", "Liam Chen")
    )

    private fun CallRecordEntity.toDomain(): CallRecord = CallRecord(
        id = id,
        peerName = peerName,
        peerAvatarColorHex = peerAvatarColorHex,
        callType = try { CallType.valueOf(callType) } catch (e: Exception) { CallType.VOICE },
        direction = try { CallDirection.valueOf(direction) } catch (e: Exception) { CallDirection.INCOMING },
        timestamp = timestamp,
        durationSec = durationSec,
        wasMissed = wasMissed
    )

    private fun parseReactions(json: String): List<MessageReaction> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<MessageReaction>()
        try {
            // Simple robust regex parse for reactions json
            val regex = """"emoji":"([^"]+)","userId":"([^"]+)","count":([0-9]+)""".toRegex()
            regex.findAll(json).forEach { match ->
                val (emoji, userId, count) = match.destructured
                list.add(MessageReaction(emoji, userId, count.toIntOrNull() ?: 1))
            }
        } catch (e: Exception) {
            // fallback
        }
        return list
    }

    private fun serializeReactions(reactions: List<MessageReaction>): String {
        return reactions.joinToString(prefix = "[", postfix = "]", separator = ",") {
            "{\"emoji\":\"${it.emoji}\",\"userId\":\"${it.userId}\",\"count\":${it.count}}"
        }
    }
}
