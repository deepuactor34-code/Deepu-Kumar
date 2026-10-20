package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.service.voice.CallStatus
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlin.math.sin

@Composable
fun VoiceVisualizer(
    status: CallStatus,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val barCount = 28
        val spacing = 6.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(3.dp.toPx())
        val maxHeight = size.height
        val centerY = size.height / 2f

        val gradient = Brush.verticalGradient(
            colors = when (status) {
                CallStatus.SPEAKING -> listOf(NeonPurple, NeonPink, NeonCyan)
                CallStatus.LISTENING -> listOf(NeonCyan, Color(0xFF00FFB2))
                CallStatus.THINKING -> listOf(NeonPink, NeonPurple)
                else -> listOf(NeonPurple.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))
            }
        )

        for (i in 0 until barCount) {
            val progress = i.toFloat() / barCount
            val wave = sin((progress * 4f * Math.PI + phase).toDouble()).toFloat()

            val factor = when (status) {
                CallStatus.SPEAKING -> (0.35f + (wave * 0.25f) + (amplitude * 0.5f)).coerceIn(0.15f, 1f)
                CallStatus.LISTENING -> (0.25f + (amplitude * 0.75f) * (0.6f + wave * 0.4f)).coerceIn(0.1f, 1f)
                CallStatus.THINKING -> (0.2f + (wave * 0.25f)).coerceIn(0.1f, 0.6f)
                else -> 0.08f
            }

            val barHeight = (maxHeight * factor).coerceAtLeast(4.dp.toPx())
            val x = i * (barWidth + spacing)
            val y = centerY - (barHeight / 2f)

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
