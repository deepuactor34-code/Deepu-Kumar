package com.example.service.gemini

import com.example.data.entity.MemoryEntity
import com.example.data.preference.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient(private val preferences: UserPreferences) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        userPrompt: String,
        conversationHistory: List<GeminiMessage> = emptyList(),
        memories: List<MemoryEntity> = emptyList(),
        imageBase64: String? = null
    ): GeminiResult = withContext(Dispatchers.IO) {
        val apiKey = preferences.getEffectiveApiKey()
        if (apiKey.isBlank()) {
            return@withContext GeminiResult.Error(
                message = "Gemini API key is not configured. Please add your API key in Settings.",
                isAuthError = true
            )
        }

        val model = preferences.getModel()
        val personalityStr = preferences.getPersonality()
        val personalityMode = try {
            PersonalityMode.valueOf(personalityStr)
        } catch (_: Exception) {
            PersonalityMode.GIRLFRIEND_WARM
        }
        val languagePref = preferences.getLanguage()
        val userName = preferences.getUserName()

        val systemPrompt = buildSystemPrompt(personalityMode, languagePref, userName, memories)

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        try {
            val rootJson = JSONObject()

            // System Instruction
            val sysInstructionObj = JSONObject()
            val sysPartsArr = JSONArray()
            val sysPartObj = JSONObject().put("text", systemPrompt)
            sysPartsArr.put(sysPartObj)
            sysInstructionObj.put("parts", sysPartsArr)
            rootJson.put("system_instruction", sysInstructionObj)

            // Contents array
            val contentsArr = JSONArray()

            // Add previous conversation turns
            for (msg in conversationHistory.takeLast(10)) {
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.role == "user") "user" else "model")
                val partsArr = JSONArray()
                val partObj = JSONObject().put("text", msg.text)
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
            }

            // Current user message
            val currentMsgObj = JSONObject()
            currentMsgObj.put("role", "user")
            val currentPartsArr = JSONArray()

            if (!imageBase64.isNullOrBlank()) {
                val inlineDataObj = JSONObject()
                inlineDataObj.put("mime_type", "image/jpeg")
                inlineDataObj.put("data", imageBase64)
                val imagePartObj = JSONObject().put("inline_data", inlineDataObj)
                currentPartsArr.put(imagePartObj)
            }

            val textPartObj = JSONObject().put("text", userPrompt)
            currentPartsArr.put(textPartObj)

            currentMsgObj.put("parts", currentPartsArr)
            contentsArr.put(currentMsgObj)

            rootJson.put("contents", contentsArr)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", if (personalityMode == PersonalityMode.TECH_CODER) 0.3 else 0.85)
            genConfig.put("topP", 0.95)
            genConfig.put("maxOutputTokens", 2048)
            rootJson.put("generationConfig", genConfig)

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(response.code, responseBody)
                return@withContext GeminiResult.Error(
                    message = errorMsg,
                    isAuthError = response.code == 400 || response.code == 401 || response.code == 403
                )
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val replyText = parts.getJSONObject(0).optString("text", "")
                    if (replyText.isNotBlank()) {
                        return@withContext GeminiResult.Success(replyText.trim())
                    }
                }
            }

            return@withContext GeminiResult.Error("No text response generated by Gemini.")
        } catch (e: Exception) {
            return@withContext GeminiResult.Error(
                message = "Connection error: ${e.localizedMessage ?: "Unable to reach Gemini API. Please check your internet connection."}"
            )
        }
    }

    suspend fun testConnection(apiKey: String, model: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Pair(false, "API Key is empty.")
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        try {
            val rootJson = JSONObject()
            val contentsArr = JSONArray()
            val item = JSONObject()
            item.put("role", "user")
            val parts = JSONArray()
            parts.put(JSONObject().put("text", "Hello, reply with only the word OK."))
            item.put("parts", parts)
            contentsArr.put(item)
            rootJson.put("contents", contentsArr)

            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Pair(true, "Successfully connected to Gemini ($model)!")
            } else {
                val errMsg = parseErrorMessage(response.code, body)
                Pair(false, errMsg)
            }
        } catch (e: Exception) {
            Pair(false, "Failed to connect: ${e.localizedMessage}")
        }
    }

    private fun buildSystemPrompt(
        personality: PersonalityMode,
        language: String,
        userName: String,
        memories: List<MemoryEntity>
    ): String {
        val memoryBuilder = StringBuilder()
        if (memories.isNotEmpty()) {
            memoryBuilder.append("\n\nStored facts and memories about $userName that you remember:\n")
            memories.forEach {
                memoryBuilder.append("- [${it.category.uppercase()}]: ${it.title} - ${it.content}\n")
            }
        }

        return """
            ${personality.systemPromptFragment}
            
            User's Name: $userName
            Preferred Language Style: $language
            (If language is 'Hinglish', mix Hindi and English naturally as spoken in modern conversational Indian context).
            
            IMPORTANT GUIDELINES:
            1. You are Yesmin, a loving AI girlfriend and high-tech personal assistant.
            2. When in girlfriend mode, be affectionate, use sweet emojis, show authentic care for $userName, ask how they are feeling, and bring joy.
            3. When answering automation or command requests (e.g., set alarm, open YouTube, play music, call someone), acknowledge warmly and clearly tell the user what action is happening.
            4. Keep voice calling replies concise, conversational, and natural to listen to (avoid huge walls of text during calls).
            5. In coding mode, provide clean, exact code with concise explanations.
            $memoryBuilder
        """.trimIndent()
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        return try {
            val json = JSONObject(body)
            val errorObj = json.optJSONObject("error")
            val message = errorObj?.optString("message")
            when {
                !message.isNullOrBlank() -> "Error ($code): $message"
                code == 400 -> "Bad Request ($code): Check if the selected model supports this feature or verify API key."
                code == 403 || code == 401 -> "Authentication error ($code): Invalid or expired Gemini API key."
                code == 429 -> "Rate limit reached ($code): Too many requests or quota exceeded."
                else -> "Server error ($code): Please try again later."
            }
        } catch (_: Exception) {
            "Server error ($code)"
        }
    }
}
