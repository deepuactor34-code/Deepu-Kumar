package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.ChatEntity
import com.example.data.entity.MemoryEntity
import com.example.data.preference.UserPreferences
import com.example.data.repository.ChatRepository
import com.example.data.repository.MemoryRepository
import com.example.service.automation.AutomationAction
import com.example.service.automation.AutomationResult
import com.example.service.automation.PhoneAutomationManager
import com.example.service.gemini.GeminiApiClient
import com.example.service.gemini.GeminiMessage
import com.example.service.gemini.GeminiResult
import com.example.service.voice.VoiceCallState
import com.example.service.voice.VoiceSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class YesminViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val memoryRepository = MemoryRepository(database.memoryDao())
    val chatRepository = ChatRepository(database.chatDao())
    val preferences = UserPreferences(application)
    val automationManager = PhoneAutomationManager(application)
    val geminiClient = GeminiApiClient(preferences)

    val memories: StateFlow<List<MemoryEntity>> = memoryRepository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatEntity>> = chatRepository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _connectionTestResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val connectionTestResult: StateFlow<Pair<Boolean, String>?> = _connectionTestResult.asStateFlow()

    private val _lastAutomation = MutableStateFlow<AutomationResult?>(null)
    val lastAutomation: StateFlow<AutomationResult?> = _lastAutomation.asStateFlow()

    val voiceManager = VoiceSpeechManager(
        context = application,
        preferences = preferences,
        scope = viewModelScope,
        onUserSpokenMessage = { spokenText -> handleSpokenCallInput(spokenText) }
    )

    val callState: StateFlow<VoiceCallState> = voiceManager.callState

    init {
        seedInitialMemoriesIfNeeded()
    }

    private fun seedInitialMemoriesIfNeeded() {
        viewModelScope.launch {
            val existing = memoryRepository.getNonSensitiveMemoriesList()
            if (existing.isEmpty()) {
                memoryRepository.insertMemory(
                    MemoryEntity(
                        title = "Favorite Language",
                        content = "User loves Hinglish and conversational friendly chats.",
                        category = "preference"
                    )
                )
                memoryRepository.insertMemory(
                    MemoryEntity(
                        title = "Goal",
                        content = "Building real Android apps using Kotlin and Termux.",
                        category = "work"
                    )
                )
            }
        }
    }

    fun sendChatMessage(input: String, imageBase64: String? = null) {
        val trimmed = input.trim()
        if (trimmed.isBlank() && imageBase64 == null) return

        viewModelScope.launch {
            // Save user message
            chatRepository.insertMessage(
                sender = "user",
                content = trimmed.ifBlank { "[Sent Image]" },
                mode = "chat",
                language = preferences.getLanguage()
            )

            // Check if user asked for phone automation (e.g., open YouTube, set alarm)
            val action = automationManager.detectAutomationAction(trimmed)
            if (action != null) {
                val autoResult = automationManager.executeAction(action)
                _lastAutomation.value = autoResult
            }

            _isGenerating.value = true

            // Fetch stored non-sensitive memories
            val storedMemories = memoryRepository.getNonSensitiveMemoriesList()

            // Fetch recent conversation history
            val recentEntities = chatRepository.getRecentMessages(8)
            val history = recentEntities.reversed().map {
                GeminiMessage(role = if (it.sender == "user") "user" else "model", text = it.content)
            }

            when (val result = geminiClient.generateResponse(trimmed, history, storedMemories, imageBase64)) {
                is GeminiResult.Success -> {
                    chatRepository.insertMessage(
                        sender = "yesmin",
                        content = result.text,
                        mode = "chat",
                        language = preferences.getLanguage()
                    )
                    // Auto speak response if enabled
                    voiceManager.speakStandAlone(result.text)
                }
                is GeminiResult.Error -> {
                    chatRepository.insertMessage(
                        sender = "yesmin",
                        content = "⚠️ ${result.message}",
                        mode = "chat"
                    )
                }
            }

            _isGenerating.value = false
        }
    }

    private fun handleSpokenCallInput(spokenText: String) {
        viewModelScope.launch {
            chatRepository.insertMessage(sender = "user", content = spokenText, mode = "voice_call")
            voiceManager.notifyThinking()

            // Check automation in voice call as well
            val action = automationManager.detectAutomationAction(spokenText)
            if (action != null) {
                val autoResult = automationManager.executeAction(action)
                _lastAutomation.value = autoResult
            }

            val storedMemories = memoryRepository.getNonSensitiveMemoriesList()
            val recentEntities = chatRepository.getRecentMessages(6)
            val history = recentEntities.reversed().map {
                GeminiMessage(role = if (it.sender == "user") "user" else "model", text = it.content)
            }

            when (val result = geminiClient.generateResponse(spokenText, history, storedMemories)) {
                is GeminiResult.Success -> {
                    chatRepository.insertMessage(sender = "yesmin", content = result.text, mode = "voice_call")
                    voiceManager.speak(result.text)
                }
                is GeminiResult.Error -> {
                    val fallback = "Sweetheart, I had a little connection issue: ${result.message}"
                    chatRepository.insertMessage(sender = "yesmin", content = fallback, mode = "voice_call")
                    voiceManager.speak(fallback)
                }
            }
        }
    }

    fun startVoiceCall() {
        voiceManager.startCall()
    }

    fun endVoiceCall() {
        voiceManager.endCall()
    }

    fun toggleCallMute() {
        voiceManager.toggleMute()
    }

    fun bargeIn() {
        voiceManager.bargeIn()
    }

    fun executeAutomation(action: AutomationAction) {
        val result = automationManager.executeAction(action)
        _lastAutomation.value = result
    }

    fun clearAutomationNotice() {
        _lastAutomation.value = null
    }

    // Memory operations
    fun addMemory(title: String, content: String, category: String, isSensitive: Boolean) {
        viewModelScope.launch {
            memoryRepository.insertMemory(
                MemoryEntity(
                    title = title.trim(),
                    content = content.trim(),
                    category = category.lowercase(),
                    isSensitive = isSensitive
                )
            )
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            memoryRepository.deleteMemory(memory)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            memoryRepository.clearAll()
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatRepository.clearHistory()
        }
    }

    fun testGeminiConnection() {
        viewModelScope.launch {
            _connectionTestResult.value = null
            val key = preferences.getEffectiveApiKey()
            val model = preferences.getModel()
            val result = geminiClient.testConnection(key, model)
            _connectionTestResult.value = result
        }
    }

    fun dismissTestResult() {
        _connectionTestResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
