package com.example.aurafit.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aurafit.AuraApplication
import com.example.aurafit.R
import kotlinx.coroutines.flow.first
import java.util.Calendar

class AuraNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as AuraApplication
        val repository = app.repository

        // Fetch today's progress
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1

        val todayWater = repository.getTodayTotalWater(startOfDay, endOfDay).first() ?: 0
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Trigger reminders depending on time of day and stats
        when {
            // Water reminder: If water logged is under 1500ml and it's afternoon/evening
            hour in 10..20 && todayWater < 1500 -> {
                showNotification(
                    title = "Stay Hydrated! 💧",
                    content = "You've only drank $todayWater mL of water today. Grab a glass to keep your metabolism active!"
                )
            }
            // Gym Split reminder: Morning nudge
            hour in 7..9 -> {
                val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                val dayOfWeek = dayNames[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]
                val splits = mapOf(
                    "Monday" to "Push split (Chest, Shoulders, Triceps)",
                    "Tuesday" to "Pull split (Back, Biceps)",
                    "Wednesday" to "Legs & Core split",
                    "Thursday" to "Rest / Active Recovery split",
                    "Friday" to "Push split (Chest, Shoulders, Triceps)",
                    "Saturday" to "Pull split (Back, Biceps)",
                    "Sunday" to "Legs & Cardio split"
                )
                val split = splits[dayOfWeek] ?: "Rest Day"
                showNotification(
                    title = "Today's Training: $dayOfWeek 🏋️",
                    content = "Ready to crush it? Today is scheduled for: $split."
                )
            }
            // Sleep nudge: Late night check
            hour >= 22 -> {
                showNotification(
                    title = "Time to wind down 💤",
                    content = "Consistency is key to muscle growth. Log your sleep and try to get 8 hours tonight!"
                )
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        val channelId = "aurafit_reminders"
        val notificationId = System.currentTimeMillis().toInt()

        // Create the notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "AuraFit Reminders"
            val descriptionText = "Periodic reminders for workouts, water, and sleep logging"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            // Use system default icon or any drawable if available (using android default icon as fallback)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Under Android 13, user must approve POST_NOTIFICATIONS permission
        }
    }
}
