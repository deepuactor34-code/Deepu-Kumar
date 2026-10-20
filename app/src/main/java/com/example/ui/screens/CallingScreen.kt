package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.voice.CallStatus
import com.example.ui.components.CyberCard
import com.example.ui.components.GlowingAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.components.VoiceVisualizer
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun CallingScreen(
    viewModel: YesminViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.startVoiceCall()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else if (callState.status == CallStatus.IDLE || callState.status == CallStatus.ENDED) {
            viewModel.startVoiceCall()
        }
    }

    val minutes = callState.durationSeconds / 60
    val seconds = callState.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    val statusColor = when (callState.status) {
        CallStatus.SPEAKING -> NeonPurple
        CallStatus.LISTENING -> NeonCyan
        CallStatus.THINKING -> NeonPink
        CallStatus.MUTED -> Color.Gray
        else -> NeonCyan
    }

    val statusText = when (callState.status) {
        CallStatus.CONNECTING -> "CONNECTING TO YESMIN..."
        CallStatus.LISTENING -> "YESMIN IS LISTENING..."
        CallStatus.THINKING -> "YESMIN IS THINKING..."
        CallStatus.SPEAKING -> "YESMIN IS SPEAKING..."
        CallStatus.MUTED -> "MICROPHONE MUTED"
        CallStatus.ENDED -> "CALL ENDED"
        CallStatus.IDLE -> "READY"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090814),
                        Color(0xFF13102A),
                        Color(0xFF090814)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.endVoiceCall()
                    onBack()
                },
                modifier = Modifier.testTag("call_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatusBadge(text = statusText, statusColor = statusColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDuration,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(modifier = Modifier.size(48.dp)) // Spacer balance
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Hero Avatar with dynamic glow
        GlowingAvatar(
            status = callState.status,
            amplitude = callState.audioAmplitude,
            size = 180.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name & Tagline
        Text(
            text = "Yesmin AI",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Your caring female companion & JARVIS assistant",
            fontSize = 13.sp,
            color = Color(0xFFAFAEC7),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Frequency Visualizer
        VoiceVisualizer(
            status = callState.status,
            amplitude = callState.audioAmplitude,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitles / Live Transcript Box
        val scrollState = rememberScrollState()
        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            backgroundColor = Color(0x99131126),
            borderColor = statusColor.copy(alpha = 0.35f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "LIVE CONVERSATION TRANSCRIPT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (callState.userSpeechTranscript.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "You: ",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = callState.userSpeechTranscript,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (callState.yesminSpeechTranscript.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Yesmin: ",
                            color = NeonPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = callState.yesminSpeechTranscript,
                            color = Color(0xFFE2E0F5),
                            fontSize = 13.sp
                        )
                    }
                }

                if (callState.userSpeechTranscript.isBlank() && callState.yesminSpeechTranscript.isBlank()) {
                    Text(
                        text = "Speak naturally in Hindi, Hinglish, or English. Yesmin is listening to you...",
                        color = Color(0xFF8B88A8),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // In-call Action Controls (Barge-in, Mute, End Call)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute / Unmute Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.toggleCallMute() },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMicMuted) Color(0xFF3E3C50) else Color(0xFF1D1A38))
                        .border(1.dp, Color(0xFF4A4670), CircleShape)
                        .testTag("call_mute_button")
                ) {
                    Icon(
                        imageVector = if (callState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute Microphone",
                        tint = if (callState.isMicMuted) Color(0xFFFF5252) else Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (callState.isMicMuted) "Unmute" else "Mute",
                    fontSize = 11.sp,
                    color = Color(0xFFAFAEC7)
                )
            }

            // Barge-in / Interrupt button (Active during Yesmin speech)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.bargeIn() },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF261D42))
                        .border(1.dp, NeonPurple.copy(alpha = 0.6f), CircleShape)
                        .testTag("call_interrupt_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = "Interrupt / Speak Now",
                        tint = NeonPurple
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Interrupt",
                    fontSize = 11.sp,
                    color = Color(0xFFAFAEC7)
                )
            }

            // End Call Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        viewModel.endVoiceCall()
                        onBack()
                    },
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF1744))
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "End Call",
                    fontSize = 11.sp,
                    color = Color(0xFFAFAEC7)
                )
            }
        }
    }
}
