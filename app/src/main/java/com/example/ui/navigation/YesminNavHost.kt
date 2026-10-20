package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.CallingScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.YesminViewModel

@Composable
fun YesminNavHost(
    viewModel: YesminViewModel,
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf(YesminScreen.Home.route) }
    var routeStack by remember { mutableStateOf(listOf(YesminScreen.Home.route)) }

    fun navigateTo(route: String) {
        if (currentRoute != route) {
            routeStack = routeStack + route
            currentRoute = route
        }
    }

    fun navigateBack() {
        if (routeStack.size > 1) {
            val updated = routeStack.dropLast(1)
            routeStack = updated
            currentRoute = updated.last()
        } else {
            currentRoute = YesminScreen.Home.route
        }
    }

    // Handle back button on sub-screens
    BackHandler(enabled = currentRoute != YesminScreen.Home.route) {
        navigateBack()
    }

    val isTopLevelScreen = YesminScreen.bottomNavScreens.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF090814),
        bottomBar = {
            if (isTopLevelScreen && currentRoute != YesminScreen.Call.route) {
                NavigationBar(
                    containerColor = Color(0xFF110F24),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF262244))
                ) {
                    YesminScreen.bottomNavScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        val isCall = screen.route == YesminScreen.Call.route

                        if (isCall) {
                            // Hero Center Call button in Nav Bar
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { navigateTo(screen.route) },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .offset(y = (-6).dp)
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(NeonPurple, NeonPink, NeonCyan)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Voice Call",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Call",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) NeonCyan else Color(0xFFAFAEC7)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("nav_call")
                            )
                        } else {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { navigateTo(screen.route) },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) NeonCyan else Color(0xFFAFAEC7),
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) NeonCyan else Color(0xFFAFAEC7)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color(0xFF221C42)
                                ),
                                modifier = Modifier.testTag("nav_${screen.route}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                YesminScreen.Home.route -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { navigateTo(it) }
                )
                YesminScreen.Chat.route -> ChatScreen(
                    viewModel = viewModel,
                    onNavigate = { navigateTo(it) }
                )
                YesminScreen.Call.route -> CallingScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() }
                )
                YesminScreen.Coding.route -> CodingScreen(
                    viewModel = viewModel
                )
                YesminScreen.Automation.route -> AutomationScreen(
                    viewModel = viewModel
                )
                YesminScreen.Memory.route -> MemoryScreen(
                    viewModel = viewModel
                )
                YesminScreen.Tools.route -> ToolsScreen(
                    viewModel = viewModel,
                    onNavigate = { navigateTo(it) }
                )
                YesminScreen.Settings.route -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    onNavigate = { navigateTo(it) }
                )
                YesminScreen.Permissions.route -> PermissionsScreen(
                    onBack = { navigateBack() }
                )
                YesminScreen.About.route -> AboutScreen(
                    onBack = { navigateBack() }
                )
                else -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { navigateTo(it) }
                )
            }
        }
    }
}
