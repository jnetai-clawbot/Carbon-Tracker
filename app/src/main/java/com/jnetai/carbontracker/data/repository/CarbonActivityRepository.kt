package com.jnetai.carbontracker.data.repository

import com.jnetai.carbontracker.data.dao.CarbonActivityDao
import com.jnetai.carbontracker.data.entity.CarbonActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CarbonActivityRepository(private val dao: CarbonActivityDao) {

    fun getAllActivities(): Flow<List<CarbonActivity>> = dao.getAllActivities()

    suspend fun insert(activity: CarbonActivity) = withContext(Dispatchers.IO) {
        dao.insert(activity)
    }

    suspend fun delete(activity: CarbonActivity) = withContext(Dispatchers.IO) {
        dao.delete(activity)
    }

    suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun getActivitiesInRange(startTime: Long, endTime: Long): List<CarbonActivity> =
        withContext(Dispatchers.IO) {
            dao.getActivitiesInRange(startTime, endTime)
        }

    fun getActivitiesInRangeFlow(startTime: Long, endTime: Long): Flow<List<CarbonActivity>> =
        dao.getActivitiesInRangeFlow(startTime, endTime)

    suspend fun getTotalEmissionInRange(startTime: Long, endTime: Long): Double =
        withContext(Dispatchers.IO) {
            dao.getTotalEmissionInRange(startTime, endTime) ?: 0.0
        }

    suspend fun getCategoryEmissionInRange(category: String, startTime: Long, endTime: Long): Double =
        withContext(Dispatchers.IO) {
            dao.getCategoryEmissionInRange(category, startTime, endTime) ?: 0.0
        }

    suspend fun getAllActivitiesForExport(): List<CarbonActivity> = withContext(Dispatchers.IO) {
        dao.getAllActivitiesList()
    }
}