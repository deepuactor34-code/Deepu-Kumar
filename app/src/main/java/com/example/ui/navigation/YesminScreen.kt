package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class YesminScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : YesminScreen("home", "Home", Icons.Default.Dashboard)
    object Chat : YesminScreen("chat", "Chat", Icons.Default.Chat)
    object Call : YesminScreen("call", "Voice Call", Icons.Default.Call)
    object Coding : YesminScreen("coding", "Coding", Icons.Default.Code)
    object Automation : YesminScreen("automation", "Automation", Icons.Default.PhoneAndroid)
    object Memory : YesminScreen("memory", "Memory Hub", Icons.Default.Memory)
    object Tools : YesminScreen("tools", "Tools", Icons.Default.AutoAwesome)
    object Settings : YesminScreen("settings", "Settings", Icons.Default.Settings)
    object Permissions : YesminScreen("permissions", "Privacy", Icons.Default.Security)
    object About : YesminScreen("about", "About Yesmin", Icons.Default.AutoAwesome)

    companion object {
        val bottomNavScreens = listOf(Home, Chat, Call, Coding, Tools)
    }
}
