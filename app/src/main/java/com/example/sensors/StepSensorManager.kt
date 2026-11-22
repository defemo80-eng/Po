package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Listens directly to real Android hardware motion sensors.
 * Never fabricates or simulates steps.
 */
class StepSensorManager(
    private val context: Context,
    private val onStepsEvent: (sensorKind: ActiveStepSensorKind, rawValue: Float, isCumulative: Boolean) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var activeKind: ActiveStepSensorKind = ActiveStepSensorKind.NONE
    private var isRegistered = false

    // Real accelerometer peak detection state (used only when hardware step sensors are absent)
    private var gravityFiltered = SensorManager.GRAVITY_EARTH
    private var lastPeakTimeMs = 0L
    private var wasAboveThreshold = false

    fun startListening(): ActiveStepSensorKind {
        if (isRegistered) return activeKind
        val sm = sensorManager ?: return ActiveStepSensorKind.NONE
        if (!SensorCapabilityInspector.hasActivityRecognitionPermission(context)) {
            return ActiveStepSensorKind.NONE
        }

        val stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounter != null && sm.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)) {
            activeKind = ActiveStepSensorKind.STEP_COUNTER
            isRegistered = true
            return activeKind
        }

        val stepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepDetector != null && sm.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL)) {
            activeKind = ActiveStepSensorKind.STEP_DETECTOR
            isRegistered = true
            return activeKind
        }

        val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accel != null && sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)) {
            activeKind = ActiveStepSensorKind.ACCELEROMETER_FALLBACK
            isRegistered = true
            return activeKind
        }

        activeKind = ActiveStepSensorKind.NONE
        return activeKind
    }

    fun stopListening() {
        if (isRegistered) {
            sensorManager?.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val cumulativeSteps = event.values.firstOrNull() ?: return
                if (!cumulativeSteps.isFinite() || cumulativeSteps < 0f) return
                onStepsEvent(ActiveStepSensorKind.STEP_COUNTER, cumulativeSteps, true)
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                val count = event.values.size.coerceAtLeast(1).toFloat()
                onStepsEvent(ActiveStepSensorKind.STEP_DETECTOR, count, false)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)
                gravityFiltered = 0.9f * gravityFiltered + 0.1f * magnitude
                val linearDelta = magnitude - gravityFiltered
                val now = System.currentTimeMillis()
                val threshold = 2.6f
                if (linearDelta > threshold && !wasAboveThreshold && (now - lastPeakTimeMs) > 320L) {
                    wasAboveThreshold = true
                    lastPeakTimeMs = now
                    onStepsEvent(ActiveStepSensorKind.ACCELEROMETER_FALLBACK, 1f, false)
                } else if (linearDelta < 1.1f) {
                    wasAboveThreshold = false
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
