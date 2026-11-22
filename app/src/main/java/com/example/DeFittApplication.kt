package com.example

import android.app.Application
import com.example.data.local.DeFittDatabase
import com.example.data.repository.DeFittRepository
import com.example.notifications.DeFittNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DeFittApplication : Application() {

    val database: DeFittDatabase by lazy { DeFittDatabase.getInstance(this) }
    val repository: DeFittRepository by lazy { DeFittRepository(this, database) }
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        DeFittNotificationHelper.ensureChannels(this)
        DeFittNotificationHelper.scheduleMidnightRollover(this)
        appScope.launch {
            repository.initializeCatalog()
        }
    }
}
