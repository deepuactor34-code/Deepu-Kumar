package com.example.service.automation

sealed class AutomationAction {
    data class LaunchApp(val appName: String, val packageName: String?) : AutomationAction()
    data class SearchYouTube(val query: String) : AutomationAction()
    data class SetAlarm(val hour: Int, val minute: Int, val message: String) : AutomationAction()
    data class SetTimer(val durationSeconds: Int, val message: String) : AutomationAction()
    data class AddCalendarEvent(val title: String, val description: String) : AutomationAction()
    data class OpenUrl(val url: String) : AutomationAction()
    data class OpenSettings(val settingsType: String = "general") : AutomationAction()
    data class DialPhone(val phoneNumber: String) : AutomationAction()
    data class SendSms(val phoneNumber: String, val message: String) : AutomationAction()
    object OpenCamera : AutomationAction()
    object OpenMaps : AutomationAction()
}

data class AutomationResult(
    val success: Boolean,
    val message: String,
    val actionType: String,
    val requiresUserConfirmation: Boolean = false,
    val pendingAction: AutomationAction? = null
)
