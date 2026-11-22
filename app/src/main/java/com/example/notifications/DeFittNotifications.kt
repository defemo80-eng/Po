package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.DeFittApplication
import com.example.MainActivity
import com.example.R
import com.example.sensors.SensorCapabilityInspector
import com.example.sensors.StepTrackingService
import com.example.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object DeFittNotificationHelper {
    const val CHANNEL_STEPS = "defitt_steps_channel"
    const val CHANNEL_REMINDERS = "defitt_reminders_channel"
    private const val REMINDER_NOTIF_ID = 4201

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val stepsChannel = NotificationChannel(
                CHANNEL_STEPS,
                context.getString(R.string.notification_channel_steps),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene el conteo automático de pasos de DeFitt."
                setShowBadge(false)
            }
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.notification_channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios diarios locales para caminar, entrenar y registrar tus comidas."
            }
            nm.createNotificationChannel(stepsChannel)
            nm.createNotificationChannel(reminderChannel)
        }
    }

    fun showDailyReminder(context: Context) {
        if (!SensorCapabilityInspector.hasNotificationPermission(context)) return
        ensureChannels(context)
        val openIntent = PendingIntent.getActivity(
            context,
            10,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("DeFitt · Tu progreso diario")
            .setContentText("Revisa tus pasos de hoy, registra tus comidas o completa una rutina breve.")
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(REMINDER_NOTIF_ID, notification)
    }

    fun scheduleMidnightRollover(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            5100,
            Intent(context, MidnightResetReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextMidnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextMidnight.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleDailyReminder(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            5200,
            Intent(context, DailyReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}

class MidnightResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = (context.applicationContext as DeFittApplication).repository
                val user = repo.getActiveUser()
                if (user != null) {
                    repo.ensureCurrentDay(user.id, DateUtils.todayKey())
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = (context.applicationContext as DeFittApplication).repository
                val user = repo.getActiveUser()
                if (user != null) {
                    val settings = repo.getSettings(user.id)
                    if (settings?.notificationsEnabled == true) {
                        DeFittNotificationHelper.showDailyReminder(context)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = (context.applicationContext as DeFittApplication).repository
                val user = repo.getActiveUser()
                if (user != null) {
                    repo.ensureCurrentDay(user.id, DateUtils.todayKey())
                    val settings = repo.getSettings(user.id)
                    DeFittNotificationHelper.scheduleMidnightRollover(context)
                    if (settings != null) {
                        DeFittNotificationHelper.scheduleDailyReminder(
                            context,
                            settings.notificationsEnabled,
                            settings.dailyReminderHour,
                            settings.dailyReminderMinute
                        )
                        if (settings.backgroundStepTrackingEnabled) {
                            StepTrackingService.startIfPermitted(context)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
