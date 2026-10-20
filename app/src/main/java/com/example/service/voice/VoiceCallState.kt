package com.example.service.voice

enum class CallStatus {
    IDLE,
    CONNECTING,
    LISTENING,
    THINKING,
    SPEAKING,
    MUTED,
    ENDED
}

data class VoiceCallState(
    val status: CallStatus = CallStatus.IDLE,
    val durationSeconds: Long = 0L,
    val userSpeechTranscript: String = "",
    val yesminSpeechTranscript: String = "",
    val isMicMuted: Boolean = false,
    val isBargeInActive: Boolean = false,
    val audioAmplitude: Float = 0f,
    val errorMessage: String? = null
)
