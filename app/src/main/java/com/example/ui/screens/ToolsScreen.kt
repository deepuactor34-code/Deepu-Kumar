package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ToolsScreen(
    viewModel: YesminViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var summarizeInput by remember { mutableStateOf("") }

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
                title = "AI Tools & Utilities",
                subtitle = "Multimodal assistants, web query, translation & summaries"
            )
        }

        // 1. Web Search Integration
        item {
            CyberCard(
                borderColor = NeonCyan.copy(alpha = 0.35f),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Web Search Assistant", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search online via browser...", color = Color(0xFF6E6A8A), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("web_search_input"),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    NeonGradientButton(
                        text = "Search Web",
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                val url = "https://www.google.com/search?q=${Uri.encode(searchQuery)}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                                searchQuery = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Document & Text Summarizer
        item {
            CyberCard(
                borderColor = NeonPurple.copy(alpha = 0.35f),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Text & Document Summarizer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = summarizeInput,
                        onValueChange = { summarizeInput = it },
                        placeholder = { Text("Paste article, notes or document text to summarize...", color = Color(0xFF6E6A8A), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    NeonGradientButton(
                        text = "Summarize in Chat",
                        onClick = {
                            if (summarizeInput.isNotBlank()) {
                                viewModel.sendChatMessage("Please summarize this text into key bullet points:\n$summarizeInput")
                                onNavigate(YesminScreen.Chat.route)
                                summarizeInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Quick AI Utilities Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToolTile(
                    icon = Icons.Default.Language,
                    title = "Translate",
                    subtitle = "Hindi <-> English",
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.sendChatMessage("Help me translate: Hello, how are you feeling today?")
                        onNavigate(YesminScreen.Chat.route)
                    }
                )
                ToolTile(
                    icon = Icons.Default.CalendarMonth,
                    title = "Daily Planner",
                    subtitle = "Organize goals",
                    color = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.sendChatMessage("Help me plan my daily schedule and coding tasks today.")
                        onNavigate(YesminScreen.Chat.route)
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToolTile(
                    icon = Icons.Default.ImageSearch,
                    title = "Vision OCR",
                    subtitle = "Photo understanding",
                    color = Color(0xFF00FFB2),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.sendChatMessage("Tell me what image understanding and OCR tasks you can help me with.")
                        onNavigate(YesminScreen.Chat.route)
                    }
                )
                ToolTile(
                    icon = Icons.Default.Mic,
                    title = "Voice Notes",
                    subtitle = "Spoken journal",
                    color = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavigate(YesminScreen.Call.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun ToolTile(
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
        backgroundColor = Color(0xFF141228),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = Color(0xFFAFAEC7), fontSize = 11.sp)
        }
    }
}
