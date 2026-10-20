package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090814))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "About Yesmin AI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Hero Persona Card
        item {
            CyberCard(
                borderColor = NeonPurple.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF15122E)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonPurple, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.yesmin_avatar),
                            contentDescription = "Yesmin AI Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Yesmin AI v1.0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Female AI Girlfriend + JARVIS-Style Android Assistant",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(text = "POWERED BY GOOGLE GEMINI", statusColor = NeonPink)
                }
            }
        }

        // Story & Core Pillars
        item {
            CyberSectionHeader(
                title = "Core Capabilities",
                subtitle = "Combining emotional depth with hardware automation"
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard(
                borderColor = Color(0xFF2C2849),
                backgroundColor = Color(0xFF131126)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeaturePillar(
                        title = "1. AI Girlfriend Personality",
                        desc = "Affectionate, humorous, and emotionally supportive companion fluent in Hinglish, Hindi, and English. Speaks in a sweet, gentle female tone."
                    )
                    FeaturePillar(
                        title = "2. JARVIS Phone Automation",
                        desc = "Launch YouTube music, set alarms, start timers, open WhatsApp, dial contacts, and control apps via safe Android system Intents."
                    )
                    FeaturePillar(
                        title = "3. Real-Time Voice Calling",
                        desc = "Hands-free continuous 2-way calling with speech-to-text, animated frequency visualizer, and instant barge-in interrupt capability."
                    )
                    FeaturePillar(
                        title = "4. Coding & Termux Studio",
                        desc = "Kotlin, Java, Python code generator and step-by-step Termux terminal guide to build APKs directly on an Android phone."
                    )
                    FeaturePillar(
                        title = "5. Persistent Local Memory",
                        desc = "Remembers your preferences, goals, and facts securely stored in an on-device Room SQLite database."
                    )
                }
            }
        }

        // Build Info & Specs
        item {
            CyberCard(
                borderColor = Color(0xFF282542),
                backgroundColor = Color(0xFF0F0E20)
            ) {
                Column {
                    Text(
                        text = "SYSTEM INFORMATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAFAEC7),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Target SDK: Android 15+ (API 36)", color = Color.White, fontSize = 12.sp)
                    Text("• Framework: Kotlin & Jetpack Compose (M3)", color = Color.White, fontSize = 12.sp)
                    Text("• Storage: Room SQLite 2.7.0", color = Color.White, fontSize = 12.sp)
                    Text("• Audio: Android SpeechRecognizer + TextToSpeech", color = Color.White, fontSize = 12.sp)
                    Text("• Network: OkHttp 4.10 + Gemini REST Engine", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FeaturePillar(title: String, desc: String) {
    Column {
        Text(title, color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(desc, color = Color(0xFFD0CEE3), fontSize = 12.sp, lineHeight = 17.sp)
    }
}
