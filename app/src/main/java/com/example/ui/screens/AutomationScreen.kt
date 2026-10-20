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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun AutomationScreen(
    viewModel: YesminViewModel,
    modifier: Modifier = Modifier
) {
    val lastAutomation by viewModel.lastAutomation.collectAsStateWithLifecycle()
    var commandInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090814))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberSectionHeader(
                title = "Phone Automation",
                subtitle = "Permission-based Android system control and app integration"
            )
        }

        // Live Execution Feedback Card
        if (lastAutomation != null) {
            item {
                CyberCard(
                    borderColor = if (lastAutomation!!.success) Color(0xFF00E676) else Color(0xFFFF5252),
                    backgroundColor = Color(0xFF131D24)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(
                                text = if (lastAutomation!!.success) "EXECUTED" else "ACTION NOTICE",
                                statusColor = if (lastAutomation!!.success) Color(0xFF00E676) else Color(0xFFFF5252)
                            )
                            Text(
                                text = "Dismiss",
                                color = Color(0xFFAFAEC7),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.clearAutomationNotice() }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lastAutomation!!.message,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Voice Command Tester Input
        item {
            CyberCard(
                borderColor = NeonPurple.copy(alpha = 0.3f),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Text(
                        text = "TEST VOICE COMMAND",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        placeholder = {
                            Text(
                                "e.g. 'Play Arijit Singh on YouTube', 'Set alarm for 6:30 AM', 'Open WhatsApp'...",
                                color = Color(0xFF6E6A8A),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("automation_command_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF2C2849),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF18152E),
                            unfocusedContainerColor = Color(0xFF18152E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    NeonGradientButton(
                        text = "Execute Command",
                        onClick = {
                            if (commandInput.isNotBlank()) {
                                val action = viewModel.automationManager.detectAutomationAction(commandInput)
                                if (action != null) {
                                    viewModel.executeAutomation(action)
                                } else {
                                    viewModel.sendChatMessage("Can you automate this for me: $commandInput")
                                }
                                commandInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Direct Action Cards Grid
        item {
            Text(
                text = "POPULAR ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAFAEC7),
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.MusicVideo,
                    title = "YouTube Search",
                    subtitle = "Search songs & videos",
                    color = Color(0xFFFF0033),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction.SearchYouTube("Top trending music songs")
                        )
                    }
                )
                ActionCard(
                    icon = Icons.Default.AccessAlarm,
                    title = "Set Alarm",
                    subtitle = "Trigger Android Clock",
                    color = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction.SetAlarm(7, 0, "Yesmin Morning Wakeup")
                        )
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.HourglassTop,
                    title = "Set Timer",
                    subtitle = "5 minute countdown",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(
                            AutomationAction.SetTimer(300, "5 Min Focus Timer")
                        )
                    }
                )
                ActionCard(
                    icon = Icons.Default.CameraAlt,
                    title = "Camera",
                    subtitle = "Launch camera app",
                    color = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(AutomationAction.OpenCamera)
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.Map,
                    title = "Google Maps",
                    subtitle = "Open navigation",
                    color = Color(0xFF00E676),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(AutomationAction.OpenMaps)
                    }
                )
                ActionCard(
                    icon = Icons.Default.Settings,
                    title = "Phone Settings",
                    subtitle = "Open device settings",
                    color = Color(0xFFFFB300),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(AutomationAction.OpenSettings())
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.Call,
                    title = "Dial Phone",
                    subtitle = "Open dialer with confirmation",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(AutomationAction.DialPhone("1234567890"))
                    }
                )
                ActionCard(
                    icon = Icons.Default.Sms,
                    title = "Send SMS",
                    subtitle = "Draft SMS with confirmation",
                    color = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.executeAutomation(AutomationAction.SendSms("1234567890", "Hello from Yesmin AI"))
                    }
                )
            }
        }

        // Accessibility & Android Security Safeguard Notice
        item {
            CyberCard(
                borderColor = Color(0xFF322E54),
                backgroundColor = Color(0xFF100E22)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURITY & ACCESSIBILITY POLICY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Yesmin AI strictly respects Android permission boundaries. Actions like calling, messaging, and alarms open the official Android system interfaces requiring explicit user confirmation. Yesmin never bypasses security policies.",
                        fontSize = 12.sp,
                        color = Color(0xFFAFAEC7),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = modifier,
        borderColor = color.copy(alpha = 0.3f),
        backgroundColor = Color(0xFF15132B),
        onClick = onClick
    ) {
        Column {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = Color(0xFFAFAEC7),
                fontSize = 11.sp
            )
        }
    }
}
