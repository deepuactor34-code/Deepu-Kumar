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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CodeBlockView
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun CodingScreen(
    viewModel: YesminViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var codePrompt by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("Kotlin") }
    var isBeginnerMode by remember { mutableStateOf(true) }

    val languages = listOf("Kotlin", "Java", "Python", "JavaScript", "HTML/CSS", "Bash")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090814))
            .padding(bottom = 80.dp)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF131126),
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Code Generator & Debugger", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Termux APK Guide", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CyberSectionHeader(
                        title = "AI Coding Assistant",
                        subtitle = "Generate, explain, and debug code in beginner-friendly terms"
                    )
                }

                // Language Selector
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languages) { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NeonPurple else Color(0xFF18152E))
                                    .clickable { selectedLanguage = lang }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = lang,
                                    color = if (isSelected) Color.Black else Color(0xFFAFAEC7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Prompt Input
                item {
                    OutlinedTextField(
                        value = codePrompt,
                        onValueChange = { codePrompt = it },
                        placeholder = {
                            Text(
                                "Describe what code you need or paste an error to debug...",
                                color = Color(0xFF6E6A8A),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("code_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0xFF2C2849),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF141228),
                            unfocusedContainerColor = Color(0xFF141228)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                // Quick Templates
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonGradientButton(
                            text = "Generate Code",
                            onClick = {
                                if (codePrompt.isNotBlank()) {
                                    val prefix = if (isBeginnerMode) {
                                        "Please generate clean $selectedLanguage code for: $codePrompt. Explain every step in simple beginner-friendly language."
                                    } else {
                                        "Generate professional $selectedLanguage code for: $codePrompt."
                                    }
                                    viewModel.sendChatMessage(prefix)
                                    codePrompt = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E1A38))
                                .clickable {
                                    if (codePrompt.isNotBlank()) {
                                        viewModel.sendChatMessage(
                                            "Please debug this code or error in $selectedLanguage, explain why it failed, and provide the fixed code:\n$codePrompt"
                                        )
                                        codePrompt = ""
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = "Debug",
                                    tint = NeonPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Debug", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Sample starter reference
                item {
                    Text(
                        text = "SAMPLE KOTLIN / JETPACK COMPOSE COMPONENT:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CodeBlockView(
                        code = """
                            @Composable
                            fun YesminGreetingCard(name: String) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    Text(
                                        text = "Hello, ${'$'}name! Yesmin is ready to help.",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        """.trimIndent(),
                        language = "kotlin",
                        filename = "YesminGreetingCard.kt"
                    )
                }
            }
        } else {
            // Termux APK Guide
            TermuxApkGuideScreen()
        }
    }
}

@Composable
private fun TermuxApkGuideScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberSectionHeader(
                title = "Termux Android APK Builder",
                subtitle = "Complete commands to compile and build APKs directly on your phone"
            )
        }

        item {
            CyberCard(
                borderColor = NeonCyan.copy(alpha = 0.4f),
                backgroundColor = Color(0xFF131126)
            ) {
                Column {
                    StatusBadge(text = "REQUIREMENTS", statusColor = NeonCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Android phone with Termux (F-Droid version recommended)\n• Minimum 4 GB RAM & 2 GB free internal storage\n• Android 8.0+ (ARM64 recommended)",
                        color = Color(0xFFE2E0F5),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "STEP 1: INSTALL REQUIRED PACKAGES IN TERMUX",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple
            )
            Spacer(modifier = Modifier.height(6.dp))
            CodeBlockView(
                code = """
                    # Update packages and install JDK 17, git, wget & unzip
                    pkg update -y && pkg upgrade -y
                    pkg install -y openjdk-17 git wget unzip clang
                    
                    # Verify Java version (must be Java 17)
                    java -version
                """.trimIndent(),
                language = "bash",
                filename = "01_install_termux_packages.sh"
            )
        }

        item {
            Text(
                text = "STEP 2: EXTRACT PROJECT ZIP & PERMISSIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple
            )
            Spacer(modifier = Modifier.height(6.dp))
            CodeBlockView(
                code = """
                    # Grant Termux storage access to read Downloads
                    termux-setup-storage
                    
                    # Create project directory and unzip project
                    mkdir -p ~/projects
                    cd ~/projects
                    unzip ~/storage/downloads/YesminAI.zip -d yesmin_app
                    cd yesmin_app
                    
                    # Make gradlew executable
                    chmod +x gradlew
                """.trimIndent(),
                language = "bash",
                filename = "02_extract_project.sh"
            )
        }

        item {
            Text(
                text = "STEP 3: COMPILE & ASSEMBLE DEBUG APK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple
            )
            Spacer(modifier = Modifier.height(6.dp))
            CodeBlockView(
                code = """
                    # Set Android SDK path if using commandline tools:
                    export ANDROID_HOME=${'$'}HOME/android-sdk
                    export PATH=${'$'}PATH:${'$'}ANDROID_HOME/cmdline-tools/latest/bin
                    
                    # Build debug APK using Gradle wrapper
                    ./gradlew assembleDebug --no-daemon
                """.trimIndent(),
                language = "bash",
                filename = "03_build_apk.sh"
            )
        }

        item {
            Text(
                text = "STEP 4: LOCATE AND INSTALL THE APK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            CodeBlockView(
                code = """
                    # Copy generated APK to phone's Download folder
                    cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/YesminAI-Debug.apk
                    
                    # Open your phone's File Manager -> Downloads -> YesminAI-Debug.apk to install!
                """.trimIndent(),
                language = "bash",
                filename = "04_install_apk.sh"
            )
        }
    }
}
