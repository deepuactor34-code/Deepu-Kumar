package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun PermissionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

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
                    text = "Permissions & Privacy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        item {
            CyberSectionHeader(
                title = "Android Device Permissions",
                subtitle = "Transparent explanation of access requested by Yesmin AI"
            )
        }

        // Microphone
        item {
            PermissionCard(
                icon = Icons.Default.Mic,
                title = "Microphone (RECORD_AUDIO)",
                description = "Required for real-time 2-way voice calling mode and spoken chat recognition. Audio is processed directly through Android's SpeechRecognizer.",
                isGranted = hasMicPermission,
                onRequest = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
            )
        }

        // Clock & Alarms
        item {
            PermissionCard(
                icon = Icons.Default.Alarm,
                title = "Alarms & Timers (SET_ALARM)",
                description = "Used to set alarms and focus countdown timers via the official Android AlarmClock intents.",
                isGranted = true,
                onRequest = null
            )
        }

        // Local Storage
        item {
            PermissionCard(
                icon = Icons.Default.Storage,
                title = "Local Encrypted Database",
                description = "Memories, preferences, and conversations are stored locally in Room SQLite database. Private conversation data is never uploaded to unauthorized servers.",
                isGranted = true,
                onRequest = null
            )
        }

        // Open App Settings button
        item {
            NeonGradientButton(
                text = "Open Android App System Settings",
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Transparency Declaration
        item {
            CyberCard(
                borderColor = Color(0xFF383457),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI COMPANION TRANSPARENCY", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Yesmin AI is an artificial intelligence companion. While Yesmin has an affectionate, caring girlfriend persona with deep warmth and humor, she is an AI assistant designed to provide emotional support, productivity, coding help, and phone automation.",
                        color = Color(0xFFE2E0F5),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequest: (() -> Unit)?
) {
    CyberCard(
        borderColor = if (isGranted) NeonCyan.copy(alpha = 0.3f) else Color(0xFFFFB300).copy(alpha = 0.4f),
        backgroundColor = Color(0xFF141228)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = title, tint = if (isGranted) NeonCyan else Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                StatusBadge(
                    text = if (isGranted) "GRANTED" else "REQUIRED",
                    statusColor = if (isGranted) Color(0xFF00E676) else Color(0xFFFFB300)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, color = Color(0xFFAFAEC7), fontSize = 12.sp, lineHeight = 18.sp)

            if (!isGranted && onRequest != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Grant Permission", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
