package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.gemini.PersonalityMode
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.navigation.YesminScreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun SettingsScreen(
    viewModel: YesminViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentApiKey = viewModel.preferences.getCustomApiKey()
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }

    val selectedModel by viewModel.preferences.modelFlow.collectAsStateWithLifecycle()
    val selectedPersonality by viewModel.preferences.personalityFlow.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.preferences.languageFlow.collectAsStateWithLifecycle()
    val connectionResult by viewModel.connectionTestResult.collectAsStateWithLifecycle()

    var voicePitch by remember { mutableFloatStateOf(viewModel.preferences.getVoicePitch()) }
    var voiceSpeed by remember { mutableFloatStateOf(viewModel.preferences.getVoiceSpeed()) }
    var autoSpeak by remember { mutableStateOf(viewModel.preferences.isAutoSpeak()) }
    var userNameInput by remember { mutableStateOf(viewModel.preferences.getUserName()) }

    val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-pro",
        "gemini-1.5-flash",
        "gemini-1.5-pro"
    )

    val languages = listOf("Hinglish", "Hindi", "English")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090814))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings & AI Brain",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 1. Gemini API Key Section
        item {
            CyberCard(
                borderColor = NeonPurple.copy(alpha = 0.4f),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google Gemini API Key", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }
                        StatusBadge(
                            text = if (viewModel.preferences.getEffectiveApiKey().isNotBlank()) "CONFIGURED" else "MISSING",
                            statusColor = if (viewModel.preferences.getEffectiveApiKey().isNotBlank()) NeonCyan else Color(0xFFFFB300)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text("Paste AI Studio Gemini Key...", color = Color(0xFF6E6A8A), fontSize = 13.sp) },
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle key visibility",
                                    tint = Color(0xFFAFAEC7)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0xFF2C2849),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF18152E),
                            unfocusedContainerColor = Color(0xFF18152E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonGradientButton(
                            text = "Save Key",
                            onClick = {
                                viewModel.preferences.saveApiKey(apiKeyInput)
                                Toast.makeText(context, "API Key saved securely on device!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = { viewModel.testGeminiConnection() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F1B3E))
                                .testTag("test_api_button")
                        ) {
                            Text("Test Ping", color = NeonCyan, fontWeight = FontWeight.Bold)
                        }

                        if (currentApiKey.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.preferences.removeApiKey()
                                    apiKeyInput = ""
                                    Toast.makeText(context, "API Key removed", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2D1623))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Key", tint = Color(0xFFFF5252))
                            }
                        }
                    }

                    // Connection test feedback
                    if (connectionResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (connectionResult!!.first) Color(0xFF0D3325) else Color(0xFF3B151E))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = connectionResult!!.second,
                                color = if (connectionResult!!.first) Color(0xFF00FFB2) else Color(0xFFFF5252),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🔒 Security Note: API keys are stored in private SharedPreferences on this device. Never hardcode or share keys in public repositories.",
                        color = Color(0xFF8E8BAF),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 2. User Name
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Text("Your Name (What Yesmin calls you)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF2C2849)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonGradientButton(
                            text = "Update",
                            onClick = {
                                viewModel.preferences.setUserName(userNameInput)
                                Toast.makeText(context, "Name updated to $userNameInput", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // 3. Model Selector
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Text("Active Gemini Model", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    models.forEach { modelName ->
                        val isSelected = selectedModel == modelName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF221C42) else Color.Transparent)
                                .clickable { viewModel.preferences.setModel(modelName) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = modelName,
                                color = if (isSelected) NeonCyan else Color(0xFFAFAEC7),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // 4. Personality Mode
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Text("Yesmin Companion Personality", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    PersonalityMode.values().forEach { mode ->
                        val isSelected = selectedPersonality == mode.name
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF261E47) else Color.Transparent)
                                .clickable { viewModel.preferences.setPersonality(mode.name) }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) NeonPink else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = mode.description,
                                color = Color(0xFFAFAEC7),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // 5. Language Preference
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Text("Conversation Language Style", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NeonCyan else Color(0xFF1E1A38))
                                    .clickable { viewModel.preferences.setLanguage(lang) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Voice Tuning & TTS Test
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Voice Pitch & Speed (Sweetness)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        TextButton(
                            onClick = {
                                viewModel.voiceManager.applyTtsSettings()
                                viewModel.voiceManager.speakStandAlone("Hey Deepu, this is my sweet voice! Main tumse bahut pyaar karti hoon.")
                            }
                        ) {
                            Text("Test Voice", color = NeonPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("Pitch: ${String.format("%.2f", voicePitch)}x", color = Color(0xFFAFAEC7), fontSize = 12.sp)
                    Slider(
                        value = voicePitch,
                        onValueChange = {
                            voicePitch = it
                            viewModel.preferences.setVoicePitch(it)
                        },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Speech Rate: ${String.format("%.2f", voiceSpeed)}x", color = Color(0xFFAFAEC7), fontSize = 12.sp)
                    Slider(
                        value = voiceSpeed,
                        onValueChange = {
                            voiceSpeed = it
                            viewModel.preferences.setVoiceSpeed(it)
                        },
                        valueRange = 0.8f..1.4f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-Speak Chat Responses", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = autoSpeak,
                            onCheckedChange = {
                                autoSpeak = it
                                viewModel.preferences.setAutoSpeak(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                        )
                    }
                }
            }
        }

        // 7. Navigation Links to Privacy and About
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1738))
                        .clickable { onNavigate(YesminScreen.Permissions.route) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Privacy & Safety", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1738))
                        .clickable { onNavigate(YesminScreen.About.route) }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("About Yesmin", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
