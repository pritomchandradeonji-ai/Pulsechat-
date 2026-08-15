package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.PulseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PulseViewModel(application: Application) : AndroidViewModel(application) {
    val repository = PulseRepository(application)

    // Current User & Global Settings
    val currentUser = repository.currentUser
    val appName = repository.appName
    val currentLanguage = repository.currentLanguage
    val privacySettings = repository.privacySettings
    val securitySettings = repository.securitySettings
    val linkedDevices = repository.linkedDevices
    val blockedUsers = repository.blockedUsers
    val channels = repository.channels
    val communities = repository.communities
    val adminStats = repository.adminStats
    val activeCall = repository.activeCall

    // Chats & Filter
    val activeChats: StateFlow<List<Chat>> = repository.getActiveChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedChats: StateFlow<List<Chat>> = repository.getArchivedChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatFilter = MutableStateFlow("All") // All, Unread, Groups, Direct, Favorites
    val chatFilter: StateFlow<String> = _chatFilter.asStateFlow()

    val filteredChats: StateFlow<List<Chat>> = combine(activeChats, _chatFilter) { chats, filter ->
        when (filter) {
            "Unread" -> chats.filter { it.unreadCount > 0 }
            "Groups" -> chats.filter { it.type == ChatType.GROUP }
            "Direct" -> chats.filter { it.type == ChatType.DIRECT }
            "Favorites" -> chats.filter { it.isPinned }
            else -> chats
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat & Messages
    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    val currentChat: StateFlow<Chat?> = _selectedChatId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getChatById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentMessages: StateFlow<List<Message>> = _selectedChatId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getMessagesForChat(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statuses & Calls
    val allStatuses: StateFlow<List<StatusStory>> = repository.getAllStatuses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCalls: StateFlow<List<CallRecord>> = repository.getAllCalls()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Story Viewer State
    private val _activeStoryView = MutableStateFlow<StatusStory?>(null)
    val activeStoryView: StateFlow<StatusStory?> = _activeStoryView.asStateFlow()

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow("All") // All, Chats, Messages, Media, Channels
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    // Recording State
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _audioRecordingSeconds = MutableStateFlow(0)
    val audioRecordingSeconds: StateFlow<Int> = _audioRecordingSeconds.asStateFlow()

    private var audioRecordingJob: Job? = null
    private var callTimerJob: Job? = null

    // Authentication Simulated State
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authStep = MutableStateFlow("LOGIN") // LOGIN, OTP, PIN, READY
    val authStep: StateFlow<String> = _authStep.asStateFlow()

    fun selectChat(chatId: String?) {
        _selectedChatId.value = chatId
        if (chatId != null) {
            repository.markChatAsRead(chatId)
        }
    }

    fun setChatFilter(filter: String) {
        _chatFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
    }

    fun openStory(story: StatusStory?) {
        _activeStoryView.value = story
    }

    // Messaging operations
    fun sendTextMessage(chatId: String, text: String, replyTo: Message? = null) {
        if (text.isBlank()) return
        repository.sendMessage(
            chatId = chatId,
            text = text.trim(),
            type = MessageType.TEXT,
            replyToMessage = replyTo
        )
    }

    fun sendVoiceMessage(chatId: String, durationSec: Int) {
        repository.sendMessage(
            chatId = chatId,
            text = "Voice message ($durationSec seconds)",
            type = MessageType.AUDIO,
            mediaUrl = "voice_${System.currentTimeMillis()}.aac",
            mediaName = "Voice_Note.aac",
            mediaSize = "${(durationSec * 32) / 10} KB",
            mediaDurationSec = durationSec
        )
    }

    fun sendMediaMessage(chatId: String, type: MessageType, caption: String, fileName: String? = null, fileSize: String? = null, locationName: String? = null) {
        repository.sendMessage(
            chatId = chatId,
            text = if (caption.isNotBlank()) caption else (fileName ?: "Attachment"),
            type = type,
            mediaUrl = "media_${System.currentTimeMillis()}",
            mediaName = fileName ?: "Media_Attachment",
            mediaSize = fileSize ?: "2.4 MB",
            locationName = locationName,
            latitude = if (type == MessageType.LOCATION) 37.7749 else null,
            longitude = if (type == MessageType.LOCATION) -122.4194 else null
        )
    }

    fun editMessage(messageId: String, newText: String) {
        repository.editMessage(messageId, newText)
    }

    fun toggleReaction(message: Message, emoji: String) {
        repository.toggleReaction(message, emoji)
    }

    fun toggleMessageStar(messageId: String) {
        repository.toggleMessageStar(messageId)
    }

    fun toggleMessagePin(messageId: String) {
        repository.toggleMessagePin(messageId)
    }

    fun deleteMessageForMe(messageId: String) {
        repository.deleteMessageForMe(messageId)
    }

    fun deleteMessageForEveryone(messageId: String) {
        repository.deleteMessageForEveryone(messageId)
    }

    fun toggleChatPin(chatId: String) {
        repository.toggleChatPin(chatId)
    }

    fun toggleChatMute(chatId: String) {
        repository.toggleChatMute(chatId)
    }

    fun toggleChatArchive(chatId: String) {
        repository.toggleChatArchive(chatId)
    }

    fun createChat(name: String, isGroup: Boolean, description: String? = null) {
        repository.createNewChat(name, isGroup, description)
    }

    fun postStory(content: String, type: StoryType, gradientHex: Long) {
        repository.postStatusStory(content, type, gradientHex)
    }

    // Voice recording simulation
    fun startAudioRecording() {
        _isRecordingAudio.value = true
        _audioRecordingSeconds.value = 0
        audioRecordingJob?.cancel()
        audioRecordingJob = viewModelScope.launch {
            while (_isRecordingAudio.value) {
                delay(1000)
                _audioRecordingSeconds.value += 1
            }
        }
    }

    fun cancelAudioRecording() {
        _isRecordingAudio.value = false
        audioRecordingJob?.cancel()
        _audioRecordingSeconds.value = 0
    }

    fun finishAudioRecording(chatId: String) {
        val duration = _audioRecordingSeconds.value.coerceAtLeast(1)
        _isRecordingAudio.value = false
        audioRecordingJob?.cancel()
        _audioRecordingSeconds.value = 0
        sendVoiceMessage(chatId, duration)
    }

    // Call management
    fun startCall(peerName: String, peerAvatarColor: Long, callType: CallType) {
        repository.startCall(peerName, peerAvatarColor, callType)
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (repository.activeCall.value != null) {
                delay(1000)
                repository.updateActiveCall { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun toggleCallMute() {
        repository.updateActiveCall { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        repository.updateActiveCall { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleCallVideo() {
        repository.updateActiveCall { it.copy(isVideoEnabled = !it.isVideoEnabled) }
    }

    fun flipCamera() {
        repository.updateActiveCall { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        repository.endActiveCall()
    }

    // Channel and Communities
    fun votePoll(channelId: String, postId: String, optionId: String) {
        repository.votePoll(channelId, postId, optionId)
    }

    fun toggleChannelSubscription(channelId: String) {
        repository.toggleChannelSubscription(channelId)
    }

    fun createChannel(name: String, handle: String, description: String) {
        repository.createChannel(name, handle, description)
    }

    fun createCommunity(name: String, description: String) {
        repository.createCommunity(name, description)
    }

    // Settings and Admin
    fun updateAppName(name: String) {
        repository.updateAppName(name)
    }

    fun setLanguage(lang: String) {
        repository.setAppLanguage(lang)
    }

    fun updateProfile(name: String, username: String, bio: String, phone: String, email: String) {
        repository.updateProfile(name, username, bio, phone, email)
    }

    fun terminateSession(sessionId: String) {
        repository.terminateDeviceSession(sessionId)
    }

    fun toggleBiometricLock() {
        repository.updateSecuritySettings { it.copy(biometricLockEnabled = !it.biometricLockEnabled) }
    }

    fun toggleTwoStepVerification(pin: String = "482910") {
        repository.updateSecuritySettings {
            it.copy(
                twoStepVerificationEnabled = !it.twoStepVerificationEnabled,
                twoStepPin = if (!it.twoStepVerificationEnabled) pin else "",
                recoveryEmail = if (!it.twoStepVerificationEnabled) "alex.recovery@pulse.io" else ""
            )
        }
    }

    fun setPrivacyLastSeen(level: PrivacyLevel) {
        repository.updatePrivacySettings { it.copy(lastSeenPrivacy = level) }
    }

    fun setPrivacyProfilePhoto(level: PrivacyLevel) {
        repository.updatePrivacySettings { it.copy(profilePhotoPrivacy = level) }
    }

    fun toggleReadReceipts() {
        repository.updatePrivacySettings { it.copy(readReceiptsEnabled = !it.readReceiptsEnabled) }
    }

    fun blockUser(userId: String) {
        repository.blockUser(userId)
    }

    fun unblockUser(userId: String) {
        repository.unblockUser(userId)
    }

    fun logout() {
        _isAuthenticated.value = false
        _authStep.value = "LOGIN"
    }

    fun authenticateSuccess() {
        _isAuthenticated.value = true
    }
}
