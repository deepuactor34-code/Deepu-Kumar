package com.example.service.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.preference.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceSpeechManager(
    private val context: Context,
    private val preferences: UserPreferences,
    private val scope: CoroutineScope,
    private val onUserSpokenMessage: (String) -> Unit
) {
    private val _callState = MutableStateFlow(VoiceCallState())
    val callState: StateFlow<VoiceCallState> = _callState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var speechRecognizer: SpeechRecognizer? = null
    private var timerJob: Job? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private var isCallActive = false
    private var isCurrentlyRecognizing = false

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                applyTtsSettings()
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _callState.update { it.copy(status = CallStatus.SPEAKING) }
                    }

                    override fun onDone(utteranceId: String?) {
                        if (isCallActive && !_callState.value.isMicMuted) {
                            mainHandler.post {
                                startListening()
                            }
                        } else if (isCallActive) {
                            _callState.update { it.copy(status = CallStatus.MUTED) }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        if (isCallActive) {
                            mainHandler.post {
                                startListening()
                            }
                        }
                    }
                })
            }
        }
    }

    fun applyTtsSettings() {
        val tts = textToSpeech ?: return
        val lang = preferences.getLanguage()
        val locale = when (lang) {
            "Hindi" -> Locale("hi", "IN")
            else -> Locale("en", "IN")
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.language = Locale.US
        }
        tts.setPitch(preferences.getVoicePitch())
        tts.setSpeechRate(preferences.getVoiceSpeed())
    }

    fun startCall() {
        if (isCallActive) return
        isCallActive = true
        _callState.update {
            VoiceCallState(
                status = CallStatus.CONNECTING,
                durationSeconds = 0,
                userSpeechTranscript = "",
                yesminSpeechTranscript = "Connecting to Yesmin...",
                isMicMuted = false
            )
        }

        startTimer()

        // Greeting from Yesmin
        val greeting = when (preferences.getPersonality()) {
            "GIRLFRIEND_WARM" -> "Hey sweetheart! I was waiting for you. How are you doing right now?"
            "PLAYFUL_HUMOROUS" -> "Hey! Your favorite girl is here. What's the plan today?"
            "JARVIS_ASSISTANT" -> "Yesmin online and listening. Systems ready for your command."
            "TECH_CODER" -> "Hey! Ready to code. What project are we building?"
            else -> "Hey Deepu! I'm so happy you called. How can I help you today?"
        }

        speak(greeting, isInitialGreeting = true)
    }

    fun endCall() {
        isCallActive = false
        stopTimer()
        stopListening()
        stopSpeaking()
        _callState.update {
            it.copy(
                status = CallStatus.ENDED,
                audioAmplitude = 0f
            )
        }
        mainHandler.postDelayed({
            _callState.update { it.copy(status = CallStatus.IDLE) }
        }, 1200)
    }

    fun toggleMute() {
        val newMuted = !_callState.value.isMicMuted
        _callState.update {
            it.copy(
                isMicMuted = newMuted,
                status = if (newMuted) CallStatus.MUTED else CallStatus.LISTENING
            )
        }
        if (newMuted) {
            stopListening()
        } else if (isCallActive && _callState.value.status != CallStatus.SPEAKING && _callState.value.status != CallStatus.THINKING) {
            startListening()
        }
    }

    /**
     * Barge-in: immediately halts Yesmin's speech and begins listening to the user
     */
    fun bargeIn() {
        if (!isCallActive) return
        stopSpeaking()
        _callState.update {
            it.copy(
                isBargeInActive = true,
                status = CallStatus.LISTENING,
                yesminSpeechTranscript = "${it.yesminSpeechTranscript} [Interrupted]"
            )
        }
        startListening()
    }

    fun speak(text: String, isInitialGreeting: Boolean = false) {
        if (!isTtsInitialized) {
            initializeTts()
        }
        applyTtsSettings()
        _callState.update {
            it.copy(
                status = CallStatus.SPEAKING,
                yesminSpeechTranscript = text
            )
        }

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "yesmin_utterance_${System.currentTimeMillis()}")
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "yesmin_utterance")
    }

    fun speakStandAlone(text: String) {
        if (!preferences.isAutoSpeak()) return
        if (!isTtsInitialized) {
            initializeTts()
        }
        applyTtsSettings()
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "standalone_${System.currentTimeMillis()}")
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "standalone")
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    fun notifyThinking() {
        _callState.update { it.copy(status = CallStatus.THINKING) }
    }

    fun startListening() {
        if (!isCallActive || _callState.value.isMicMuted) return

        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                }

                speechRecognizer?.setRecognitionListener(createRecognitionListener())

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    val lang = preferences.getLanguage()
                    val langCode = when (lang) {
                        "Hindi" -> "hi-IN"
                        else -> "en-IN"
                    }
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }

                isCurrentlyRecognizing = true
                _callState.update {
                    it.copy(
                        status = CallStatus.LISTENING,
                        isBargeInActive = false
                    )
                }
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _callState.update {
                    it.copy(errorMessage = "Microphone error: ${e.localizedMessage}")
                }
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                isCurrentlyRecognizing = false
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } catch (_: Exception) {}
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _callState.update { it.copy(status = CallStatus.LISTENING) }
            }

            override fun onBeginningOfSpeech() {
                _callState.update { it.copy(audioAmplitude = 0.8f) }
            }

            override fun onRmsChanged(rmsdB: Float) {
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1f)
                _callState.update { it.copy(audioAmplitude = normalized) }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _callState.update { it.copy(audioAmplitude = 0.1f) }
            }

            override fun onError(error: Int) {
                isCurrentlyRecognizing = false
                // If it's just silence or no match and the call is still active, restart listening gently
                if (isCallActive && !_callState.value.isMicMuted && _callState.value.status != CallStatus.SPEAKING && _callState.value.status != CallStatus.THINKING) {
                    mainHandler.postDelayed({
                        if (isCallActive && !_callState.value.isMicMuted) {
                            startListening()
                        }
                    }, 500)
                }
            }

            override fun onResults(results: Bundle?) {
                isCurrentlyRecognizing = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                if (spokenText.isNotBlank()) {
                    _callState.update {
                        it.copy(
                            userSpeechTranscript = spokenText,
                            status = CallStatus.THINKING
                        )
                    }
                    onUserSpokenMessage(spokenText)
                } else if (isCallActive && !_callState.value.isMicMuted) {
                    mainHandler.postDelayed({ startListening() }, 400)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = partials?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    _callState.update { it.copy(userSpeechTranscript = partial) }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = scope.launch {
            while (isActive && isCallActive) {
                delay(1000)
                _callState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun release() {
        stopTimer()
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
