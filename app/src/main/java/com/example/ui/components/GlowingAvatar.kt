package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.service.voice.CallStatus
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun GlowingAvatar(
    status: CallStatus = CallStatus.IDLE,
    amplitude: Float = 0f,
    size: Dp = 160.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = when (status) {
            CallStatus.SPEAKING -> 1.25f + (amplitude * 0.3f)
            CallStatus.LISTENING -> 1.18f + (amplitude * 0.2f)
            CallStatus.THINKING -> 1.12f
            else -> 1.05f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (status) {
                    CallStatus.SPEAKING -> 800
                    CallStatus.LISTENING -> 1000
                    CallStatus.THINKING -> 600
                    else -> 2200
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = when (status) {
            CallStatus.SPEAKING -> 1.45f
            CallStatus.LISTENING -> 1.35f
            CallStatus.THINKING -> 1.2f
            else -> 1.1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (status) {
                    CallStatus.SPEAKING -> 1100
                    CallStatus.LISTENING -> 1400
                    CallStatus.THINKING -> 900
                    else -> 3000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_2"
    )

    val glowColors = when (status) {
        CallStatus.SPEAKING -> listOf(NeonPurple, NeonPink, NeonCyan)
        CallStatus.LISTENING -> listOf(NeonCyan, Color(0xFF00FFCC), NeonPurple)
        CallStatus.THINKING -> listOf(NeonPink, NeonPurple, Color(0xFFFFD600))
        CallStatus.MUTED -> listOf(Color.Gray, Color.DarkGray)
        else -> listOf(NeonPurple, NeonCyan)
    }

    Box(
        modifier = modifier.size(size * 1.5f),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing energy ring 2
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale2)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColors[0].copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Middle energy ring 1
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale1)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(glowColors),
                    shape = CircleShape
                )
        )

        // Yesmin Avatar Picture inside glowing circular boundary
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(NeonCyan, NeonPurple, NeonPink)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.yesmin_avatar),
                contentDescription = "Yesmin AI Companion Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        }
    }
}
