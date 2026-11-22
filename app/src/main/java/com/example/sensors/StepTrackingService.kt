package com.example.sensors

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.DeFittApplication
import com.example.MainActivity
import com.example.R
import com.example.notifications.DeFittNotificationHelper
import com.example.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class StepTrackingService : android.app.Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var sensorManager: StepSensorManager? = null
    private var currentActiveUserId: Long? = null

    override fun onCreate() {
        super.onCreate()
        DeFittNotificationHelper.ensureChannels(this)
        startForegroundWithNotification(steps = 0, distanceMeters = 0.0)
        observeActiveSessionAndSteps()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        startHardwareSensorIfPermitted()
        return START_STICKY
    }

    private fun startHardwareSensorIfPermitted() {
        if (sensorManager == null) {
            sensorManager = StepSensorManager(this) { kind, raw, isCumulative ->
                val userId = currentActiveUserId ?: return@StepSensorManager
                serviceScope.launch {
                    val repo = (application as DeFittApplication).repository
                    repo.recordStepSensorEvent(
                        userId = userId,
                        sensorType = kind.code,
                        rawValue = raw,
                        isCumulativeCounter = isCumulative,
                        dateKey = DateUtils.todayKey()
                    )
                }
            }
        }
        sensorManager?.startListening()
    }

    private fun observeActiveSessionAndSteps() {
        val repo = (application as DeFittApplication).repository
        serviceScope.launch {
            repo.observeActiveUser().collectLatest { user ->
                currentActiveUserId = user?.id
                if (user == null) {
                    stopSelf()
                    return@collectLatest
                }
                val settings = repo.getSettings(user.id)
                if (settings?.backgroundStepTrackingEnabled == false) {
                    stopSelf()
                    return@collectLatest
                }
                repo.ensureCurrentDay(user.id)
                repo.observeDailySteps(user.id, DateUtils.todayKey()).collectLatest { daily ->
                    updateNotification(
                        steps = daily?.steps ?: 0,
                        distanceMeters = daily?.distanceMeters ?: 0.0
                    )
                }
            }
        }
    }

    private fun startForegroundWithNotification(steps: Int, distanceMeters: Double) {
        val notification = buildNotification(steps, distanceMeters)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(steps: Int, distanceMeters: Double) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(steps, distanceMeters))
    }

    private fun buildNotification(steps: Int, distanceMeters: Double): Notification {
        val km = String.format(Locale.getDefault(), "%.2f km", distanceMeters / 1000.0)
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, DeFittNotificationHelper.CHANNEL_STEPS)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("DeFitt · $steps pasos hoy")
            .setContentText("Distancia estimada: $km · Conteo local activo")
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        sensorManager?.stopListening()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    companion object {
        const val NOTIFICATION_ID = 4101
        const val ACTION_STOP = "com.example.defitt.STOP_STEP_SERVICE"

        fun startIfPermitted(context: Context) {
            if (!SensorCapabilityInspector.hasActivityRecognitionPermission(context)) return
            if (SensorCapabilityInspector.detectBestSensor(context) == ActiveStepSensorKind.NONE) return
            val intent = Intent(context, StepTrackingService::class.java)
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, StepTrackingService::class.java))
        }
    }
}
