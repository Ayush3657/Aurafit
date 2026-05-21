package com.example.aurafit

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aurafit.ai.GeminiService
import com.example.aurafit.data.AuraDatabase
import com.example.aurafit.data.DataRepository
import com.example.aurafit.data.DefaultDataRepository
import com.example.aurafit.worker.AuraNotificationWorker
import java.util.concurrent.TimeUnit

class AuraApplication : Application() {
    val database by lazy { AuraDatabase.getDatabase(this) }
    val repository by lazy { DefaultDataRepository(database) }
    val geminiService by lazy { GeminiService(this) }

    override fun onCreate() {
        super.onCreate()
        setupPeriodicNotifications()
    }

    private fun setupPeriodicNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<AuraNotificationWorker>(2, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AuraPeriodicNotifications",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}


