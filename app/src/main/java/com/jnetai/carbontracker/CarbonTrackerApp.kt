package com.jnetai.carbontracker

import android.app.Application
import com.jnetai.carbontracker.data.AppDatabase
import com.jnetai.carbontracker.data.repository.CarbonActivityRepository
import com.jnetai.carbontracker.data.repository.GoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CarbonTrackerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val activityRepository by lazy { CarbonActivityRepository(database.carbonActivityDao()) }
    val goalRepository by lazy { GoalRepository(database.goalDao()) }
    val applicationScope = CoroutineScope(SupervisorJob())
}