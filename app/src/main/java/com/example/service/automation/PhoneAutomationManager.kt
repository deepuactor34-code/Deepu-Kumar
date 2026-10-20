package com.example.service.automation

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import java.util.Locale

class PhoneAutomationManager(private val context: Context) {

    /**
     * Inspects spoken text or chat message to detect actionable device automations.
     */
    fun detectAutomationAction(input: String): AutomationAction? {
        val lower = input.lowercase(Locale.ROOT).trim()

        // 1. YouTube & Music
        if (lower.contains("youtube") || lower.contains("play song") || lower.contains("play music") || lower.contains("gaana chalao")) {
            val query = cleanSearchQuery(lower)
            return AutomationAction.SearchYouTube(query.ifBlank { "Trending Music" })
        }

        // 2. Alarm
        if (lower.contains("set alarm") || lower.contains("alarm lagao") || lower.contains("wake me up at")) {
            val time = parseTimeFromText(lower)
            return AutomationAction.SetAlarm(time.first, time.second, "Yesmin Alarm")
        }

        // 3. Timer
        if (lower.contains("set timer") || lower.contains("timer lagao") || lower.contains("minute timer")) {
            val seconds = parseTimerSeconds(lower)
            return AutomationAction.SetTimer(seconds, "Yesmin Timer")
        }

        // 4. Open Camera
        if (lower.contains("open camera") || lower.contains("camera kholo") || lower.contains("take a photo")) {
            return AutomationAction.OpenCamera
        }

        // 5. Open Settings
        if (lower.contains("open settings") || lower.contains("phone settings") || lower.contains("settings kholo")) {
            return AutomationAction.OpenSettings("general")
        }

        // 6. Open Maps
        if (lower.contains("open maps") || lower.contains("google maps") || lower.contains("navigation kholo")) {
            return AutomationAction.OpenMaps
        }

        // 7. Calling
        if (lower.startsWith("call ") || lower.contains("ko call karo") || lower.startsWith("phone ")) {
            val target = lower.removePrefix("call ").removeSuffix(" ko call karo").trim()
            return AutomationAction.DialPhone(target)
        }

        // 8. Launch common apps
        if (lower.startsWith("open ") || lower.startsWith("launch ") || lower.endsWith(" kholo")) {
            val appName = lower.removePrefix("open ")
                .removePrefix("launch ")
                .removeSuffix(" kholo")
                .trim()
            val pkg = resolvePackageName(appName)
            return AutomationAction.LaunchApp(appName, pkg)
        }

        return null
    }

    /**
     * Executes the requested automation action using Android system intents.
     */
    fun executeAction(action: AutomationAction): AutomationResult {
        return try {
            when (action) {
                is AutomationAction.SearchYouTube -> {
                    val intent = Intent(Intent.ACTION_SEARCH).apply {
                        setPackage("com.google.android.youtube")
                        putExtra("query", action.query)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        AutomationResult(true, "Opening YouTube for '${action.query}'", "YouTube")
                    } else {
                        // Fallback to browser YouTube
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(action.query)}")
                        ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        context.startActivity(webIntent)
                        AutomationResult(true, "Opening YouTube in browser for '${action.query}'", "YouTube Browser")
                    }
                }

                is AutomationAction.SetAlarm -> {
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(AlarmClock.EXTRA_HOUR, action.hour)
                        putExtra(AlarmClock.EXTRA_MINUTES, action.minute)
                        putExtra(AlarmClock.EXTRA_MESSAGE, action.message)
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", action.hour, action.minute)
                        AutomationResult(true, "Setting alarm for $formattedTime", "Alarm")
                    } else {
                        AutomationResult(false, "No compatible Clock application found on this device.", "Alarm")
                    }
                }

                is AutomationAction.SetTimer -> {
                    val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                        putExtra(AlarmClock.EXTRA_LENGTH, action.durationSeconds)
                        putExtra(AlarmClock.EXTRA_MESSAGE, action.message)
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        AutomationResult(true, "Started timer for ${action.durationSeconds / 60} min", "Timer")
                    } else {
                        AutomationResult(false, "No Clock app found to set timer.", "Timer")
                    }
                }

                is AutomationAction.LaunchApp -> {
                    val pkg = action.packageName ?: resolvePackageName(action.appName)
                    if (pkg != null) {
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                        if (launchIntent != null) {
                            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(launchIntent)
                            AutomationResult(true, "Opening ${action.appName}...", "App Launcher")
                        } else {
                            AutomationResult(false, "${action.appName} is not installed or cannot be opened.", "App Launcher")
                        }
                    } else {
                        AutomationResult(false, "Could not locate app '${action.appName}'.", "App Launcher")
                    }
                }

                is AutomationAction.OpenCamera -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        AutomationResult(true, "Opening camera...", "Camera")
                    } else {
                        AutomationResult(false, "Camera app not found.", "Camera")
                    }
                }

                is AutomationAction.OpenMaps -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Opening Maps...", "Maps")
                }

                is AutomationAction.OpenSettings -> {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Opening Android Settings...", "Settings")
                }

                is AutomationAction.DialPhone -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${Uri.encode(action.phoneNumber)}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Opening dialer for ${action.phoneNumber} (user confirmation required)", "Dialer")
                }

                is AutomationAction.SendSms -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:${Uri.encode(action.phoneNumber)}")
                        putExtra("sms_body", action.message)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Drafting SMS to ${action.phoneNumber}", "SMS")
                }

                is AutomationAction.OpenUrl -> {
                    val uri = if (action.url.startsWith("http://") || action.url.startsWith("https://")) {
                        Uri.parse(action.url)
                    } else {
                        Uri.parse("https://${action.url}")
                    }
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Opening browser for ${action.url}", "Browser")
                }

                is AutomationAction.AddCalendarEvent -> {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, action.title)
                        putExtra(CalendarContract.Events.DESCRIPTION, action.description)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    AutomationResult(true, "Opening calendar to schedule '${action.title}'", "Calendar")
                }
            }
        } catch (e: Exception) {
            AutomationResult(false, "Action failed: ${e.localizedMessage}", "Automation Error")
        }
    }

    private fun resolvePackageName(appName: String): String? {
        val lower = appName.lowercase(Locale.ROOT)
        return when {
            lower.contains("youtube") -> "com.google.android.youtube"
            lower.contains("whatsapp") -> "com.whatsapp"
            lower.contains("chrome") || lower.contains("browser") -> "com.android.chrome"
            lower.contains("maps") -> "com.google.android.apps.maps"
            lower.contains("gmail") || lower.contains("mail") -> "com.google.android.gm"
            lower.contains("camera") -> null // Uses MediaStore intent
            lower.contains("play store") -> "com.android.vending"
            lower.contains("spotify") -> "com.spotify.music"
            lower.contains("instagram") -> "com.instagram.android"
            lower.contains("telegram") -> "org.telegram.messenger"
            lower.contains("termux") -> "com.termux"
            else -> {
                // Try to find matching installed package
                val pm = context.packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                for (app in packages) {
                    val label = pm.getApplicationLabel(app).toString().lowercase(Locale.ROOT)
                    if (label.contains(lower) || lower.contains(label)) {
                        return app.packageName
                    }
                }
                null
            }
        }
    }

    private fun cleanSearchQuery(text: String): String {
        return text.replace("open youtube and search for", "")
            .replace("open youtube and play", "")
            .replace("search for", "")
            .replace("play song", "")
            .replace("play music", "")
            .replace("youtube pe chalao", "")
            .replace("youtube par", "")
            .replace("gaana chalao", "")
            .replace("play", "")
            .replace("youtube", "")
            .trim()
    }

    private fun parseTimeFromText(text: String): Pair<Int, Int> {
        val regex = Regex("(\\d{1,2})(:(\\d{2}))?\\s*(am|pm)?", RegexOption.IGNORE_CASE)
        val match = regex.find(text)
        if (match != null) {
            var hour = match.groups[1]?.value?.toIntOrNull() ?: 7
            val minute = match.groups[3]?.value?.toIntOrNull() ?: 0
            val amPm = match.groups[4]?.value?.lowercase(Locale.ROOT)

            if (amPm == "pm" && hour < 12) hour += 12
            if (amPm == "am" && hour == 12) hour = 0
            return Pair(hour, minute)
        }
        return Pair(7, 0)
    }

    private fun parseTimerSeconds(text: String): Int {
        val regex = Regex("(\\d+)\\s*(minute|min|second|sec)", RegexOption.IGNORE_CASE)
        val match = regex.find(text)
        if (match != null) {
            val num = match.groups[1]?.value?.toIntOrNull() ?: 5
            val unit = match.groups[2]?.value?.lowercase(Locale.ROOT) ?: "min"
            return if (unit.startsWith("sec")) num else num * 60
        }
        return 300 // default 5 minutes
    }
}
