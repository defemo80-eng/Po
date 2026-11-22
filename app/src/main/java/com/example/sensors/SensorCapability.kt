package com.example.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat

enum class ActiveStepSensorKind(val label: String, val code: String) {
    STEP_COUNTER("Sensor de pasos (Hardware)", "TYPE_STEP_COUNTER"),
    STEP_DETECTOR("Detector de pasos", "TYPE_STEP_DETECTOR"),
    ACCELEROMETER_FALLBACK("Acelerómetro (alternativo)", "ACCELEROMETER_FALLBACK"),
    NONE("Sin sensor de movimiento", "NONE")
}

data class DeviceSensorStatus(
    val sensorKind: ActiveStepSensorKind,
    val hasActivityRecognitionPermission: Boolean,
    val hasNotificationPermission: Boolean,
    val isIgnoringBatteryOptimizations: Boolean,
    val limitationMessage: String?
)

object SensorCapabilityInspector {

    fun detectBestSensor(context: Context): ActiveStepSensorKind {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            ?: return ActiveStepSensorKind.NONE
        return when {
            sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null -> ActiveStepSensorKind.STEP_COUNTER
            sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null -> ActiveStepSensorKind.STEP_DETECTOR
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null -> ActiveStepSensorKind.ACCELEROMETER_FALLBACK
            else -> ActiveStepSensorKind.NONE
        }
    }

    fun hasActivityRecognitionPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return true
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun inspect(context: Context): DeviceSensorStatus {
        val kind = detectBestSensor(context)
        val hasActivity = hasActivityRecognitionPermission(context)
        val hasNotif = hasNotificationPermission(context)
        val batteryOk = isIgnoringBatteryOptimizations(context)

        val limitation = when {
            kind == ActiveStepSensorKind.NONE ->
                "Este dispositivo no dispone de sensor de pasos ni acelerómetro compatible."
            !hasActivity ->
                "Concede el permiso de Actividad física para contar pasos reales automáticamente."
            kind == ActiveStepSensorKind.ACCELEROMETER_FALLBACK ->
                "Tu teléfono no incluye sensor dedicado TYPE_STEP_COUNTER; DeFitt usa el acelerómetro como alternativa compatible."
            !batteryOk ->
                "El ahorro de batería de Android puede pausar el conteo cuando la app está cerrada."
            else -> null
        }

        return DeviceSensorStatus(
            sensorKind = kind,
            hasActivityRecognitionPermission = hasActivity,
            hasNotificationPermission = hasNotif,
            isIgnoringBatteryOptimizations = batteryOk,
            limitationMessage = limitation
        )
    }
}
