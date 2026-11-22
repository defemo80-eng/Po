package com.example.sensors

import android.content.Context
import android.os.SystemClock
import com.example.data.repository.DeFittRepository
import com.example.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Single process-wide owner of hardware step sensor listening and serialized DB commits.
 * Prevents duplicate SensorEventListener registration between UI and foreground service
 * and prevents concurrent read-modify-write races on TYPE_STEP_COUNTER deltas.
 */
class StepSensorCoordinator(
    private val context: Context,
    private val repository: DeFittRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    private var sensorManager: StepSensorManager? = null

    fun startListeningIfPermitted(): ActiveStepSensorKind {
        if (!SensorCapabilityInspector.hasActivityRecognitionPermission(context)) {
            return ActiveStepSensorKind.NONE
        }
        synchronized(this) {
            if (sensorManager == null) {
                sensorManager = StepSensorManager(context) { kind, rawValue, isCumulative ->
                    val eventTimeMs = System.currentTimeMillis()
                    val elapsedMs = SystemClock.elapsedRealtime()
                    scope.launch {
                        mutex.withLock {
                            val activeUser = repository.getActiveUser() ?: return@withLock
                            repository.recordStepSensorEvent(
                                userId = activeUser.id,
                                sensorType = kind.code,
                                rawValue = rawValue,
                                isCumulativeCounter = isCumulative,
                                dateKey = DateUtils.todayKey(eventTimeMs),
                                nowMs = eventTimeMs,
                                elapsedRealtimeMs = elapsedMs
                            )
                        }
                    }
                }
            }
            return sensorManager?.startListening() ?: ActiveStepSensorKind.NONE
        }
    }

    fun stopListening() {
        synchronized(this) {
            sensorManager?.stopListening()
        }
    }
}
