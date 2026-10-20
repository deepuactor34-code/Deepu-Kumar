package com.example.data.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("yesmin_user_preferences", Context.MODE_PRIVATE)

    companion object {
        const val KEY_API_KEY = "gemini_api_key"
        const val KEY_MODEL = "selected_model"
        const val KEY_PERSONALITY = "personality_mode"
        const val KEY_LANGUAGE = "preferred_language"
        const val KEY_VOICE_PITCH = "voice_pitch"
        const val KEY_VOICE_SPEED = "voice_speed"
        const val KEY_AUTO_SPEAK = "auto_speak_responses"
        const val KEY_VIBRATION = "haptic_feedback_enabled"
        const val KEY_USER_NAME = "user_display_name"

        const val DEFAULT_MODEL = "gemini-2.5-flash"
        const val DEFAULT_PERSONALITY = "GIRLFRIEND_WARM"
        const val DEFAULT_LANGUAGE = "Hinglish"
        const val DEFAULT_VOICE_PITCH = 1.18f
        const val DEFAULT_VOICE_SPEED = 1.02f
    }

    private val _apiKeyFlow = MutableStateFlow(getEffectiveApiKey())
    val apiKeyFlow: StateFlow<String> = _apiKeyFlow.asStateFlow()

    private val _modelFlow = MutableStateFlow(getModel())
    val modelFlow: StateFlow<String> = _modelFlow.asStateFlow()

    private val _personalityFlow = MutableStateFlow(getPersonality())
    val personalityFlow: StateFlow<String> = _personalityFlow.asStateFlow()

    private val _languageFlow = MutableStateFlow(getLanguage())
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    fun getCustomApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun getEffectiveApiKey(): String {
        val customKey = getCustomApiKey()
        if (customKey.isNotBlank()) return customKey
        // Fallback to BuildConfig if provided via .env/Secrets panel
        return try {
            val buildConfigKey = BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String
            buildConfigKey ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key.trim()).apply()
        _apiKeyFlow.value = getEffectiveApiKey()
    }

    fun removeApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
        _apiKeyFlow.value = getEffectiveApiKey()
    }

    fun getModel(): String {
        return prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun setModel(model: String) {
        prefs.edit().putString(KEY_MODEL, model).apply()
        _modelFlow.value = model
    }

    fun getPersonality(): String {
        return prefs.getString(KEY_PERSONALITY, DEFAULT_PERSONALITY) ?: DEFAULT_PERSONALITY
    }

    fun setPersonality(personality: String) {
        prefs.edit().putString(KEY_PERSONALITY, personality).apply()
        _personalityFlow.value = personality
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        _languageFlow.value = language
    }

    fun getVoicePitch(): Float {
        return prefs.getFloat(KEY_VOICE_PITCH, DEFAULT_VOICE_PITCH)
    }

    fun setVoicePitch(pitch: Float) {
        prefs.edit().putFloat(KEY_VOICE_PITCH, pitch).apply()
    }

    fun getVoiceSpeed(): Float {
        return prefs.getFloat(KEY_VOICE_SPEED, DEFAULT_VOICE_SPEED)
    }

    fun setVoiceSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_VOICE_SPEED, speed).apply()
    }

    fun isAutoSpeak(): Boolean {
        return prefs.getBoolean(KEY_AUTO_SPEAK, true)
    }

    fun setAutoSpeak(autoSpeak: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SPEAK, autoSpeak).apply()
    }

    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Deepu") ?: "Deepu"
    }

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name.trim()).apply()
    }
}
