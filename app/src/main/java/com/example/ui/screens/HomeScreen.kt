package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.automation.AutomationAction
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.GlowingAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.navigation.YesminScreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun HomeScreen(
    viewModel: YesminViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val apiKey = viewModel.preferences.getEffectiveApiKey()
    val personality = viewModel.preferences.getPersonality()
    val model = viewModel.preferences.getModel()
    val userName = viewModel.preferences.getUserName()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top HUD Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "YESMIN AI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = if (apiKey.isNotBlank()) "ONLINE" else "SETUP KEY",
                            statusColor = if (apiKey.isNotBlank()) NeonCyan else Color(0xFFFFB300)
                        )
                    }
                    Text(
                        text = "Namaste $userName! Main hamesha tumhare saath hoon ❤️",
                        fontSize = 13.sp,
                        color = Color(0xFFAFAEC7)
                    )
                }

                IconButton(
                    onClick = { onNavigate(YesminScreen.Settings.route) },
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = NeonPurple
                    )
                }
            }
        }

        // Hero Voice Calling Card
        item {
            CyberCard(
                borderColor = NeonPurple.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF15122C),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calling_hero_card"),
                onClick = {
                    onNavigate(YesminScreen.Call.route)
                    viewModel.startVoiceCall()
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        StatusBadge(text = "LIVE VOICE CALLING", statusColor = NeonPink)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Call Yesmin",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Continuous 2-way voice, Hindi/English & girlfriend support.",
                            fontSize = 13.sp,
                            color = Color(0xFFC7C5DD),
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NeonPurple, NeonPink)
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Start Call",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    GlowingAvatar(
                        status = callState.status,
                        amplitude = callState.audioAmplitude,
                        size = 90.dp
                    )
                }
            }
        }

        // Quick Automation Action Shortcuts
        item {
            CyberSectionHeader(
                title = "JARVIS Automation",
                subtitle = "1-Tap device actions powered by Android Intents"
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AutomationPill(
                        icon = Icons.Default.MusicNote,
                        title = "Play Songs",
                        subtitle = "YouTube Music",
                        color = NeonCyan,
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction.SearchYouTube("Arijit Singh romantic hindi songs")
                            )
                        }
                    )
                }
                item {
                    AutomationPill(
                        icon = Icons.Default.Alarm,
                        title = "Set Alarm",
                        subtitle = "7:00 AM",
                        color = NeonPurple,
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction.SetAlarm(7, 0, "Yesmin Morning Wakeup")
                            )
                        }
                    )
                }
                item {
                    AutomationPill(
                        icon = Icons.Default.CameraAlt,
                        title = "Camera",
                        subtitle = "Take Photo",
                        color = NeonPink,
                        onClick = {
                            viewModel.executeAutomation(AutomationAction.OpenCamera)
                        }
                    )
                }
                item {
                    AutomationPill(
                        icon = Icons.Default.Chat,
                        title = "WhatsApp",
                        subtitle = "Launch App",
                        color = Color(0xFF00E676),
                        onClick = {
                            viewModel.executeAutomation(
                                AutomationAction.LaunchApp("WhatsApp", "com.whatsapp")
                            )
                        }
                    )
                }
            }
        }

        // Assistant Modes Quick Jump
        item {
            CyberSectionHeader(
                title = "Capabilities",
                subtitle = "Switch between AI Girlfriend, Coding & Memory Hub"
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF141228),
                    borderColor = NeonCyan.copy(alpha = 0.3f),
                    onClick = { onNavigate(YesminScreen.Chat.route) }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI Chat",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Caring conversations, sweet Hinglish & advice.",
                            fontSize = 12.sp,
                            color = Color(0xFFAFAEC7)
                        )
                    }
                }

                CyberCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF141228),
                    borderColor = NeonPurple.copy(alpha = 0.3f),
                    onClick = { onNavigate(YesminScreen.Coding.route) }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coding & Termux",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Kotlin, APK build scripts & error fixing.",
                            fontSize = 12.sp,
                            color = Color(0xFFAFAEC7)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF141228),
                    borderColor = NeonPink.copy(alpha = 0.3f),
                    onClick = { onNavigate(YesminScreen.Memory.route) }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = NeonPink,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Memory Hub",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${memories.size} facts remembered locally in Room.",
                            fontSize = 12.sp,
                            color = Color(0xFFAFAEC7)
                        )
                    }
                }

                CyberCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFF141228),
                    borderColor = Color(0xFF00FFB2).copy(alpha = 0.3f),
                    onClick = { onNavigate(YesminScreen.Automation.route) }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF00FFB2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Automation",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Voice command intents, alarms & apps.",
                            fontSize = 12.sp,
                            color = Color(0xFFAFAEC7)
                        )
                    }
                }
            }
        }

        // Active AI System Status Banner
        item {
            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF0F0E20)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CORE SYSTEM SPECS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Gemini Brain",
                            fontSize = 11.sp,
                            color = Color(0xFFAFAEC7)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Model: $model",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "• Persona: $personality",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "• Storage: Room SQLite Database (Encrypted Device-Local)",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AutomationPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF16132C))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = Color(0xFFAFAEC7),
                    fontSize = 11.sp
                )
            }
        }
    }
}
