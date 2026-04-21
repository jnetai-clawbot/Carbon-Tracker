package com.jnetai.carbontracker.data.dao

import androidx.room.*
import com.jnetai.carbontracker.data.entity.CarbonActivity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarbonActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: CarbonActivity)

    @Delete
    suspend fun delete(activity: CarbonActivity)

    @Update
    suspend fun update(activity: CarbonActivity)

    @Query("SELECT * FROM carbon_activities ORDER BY date DESC")
    fun getAllActivities(): Flow<List<CarbonActivity>>

    @Query("SELECT * FROM carbon_activities WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    suspend fun getActivitiesInRange(startTime: Long, endTime: Long): List<CarbonActivity>

    @Query("SELECT * FROM carbon_activities WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    fun getActivitiesInRangeFlow(startTime: Long, endTime: Long): Flow<List<CarbonActivity>>

    @Query("SELECT SUM(emission) FROM carbon_activities WHERE date >= :startTime AND date <= :endTime")
    suspend fun getTotalEmissionInRange(startTime: Long, endTime: Long): Double?

    @Query("SELECT SUM(emission) FROM carbon_activities WHERE category = :category AND date >= :startTime AND date <= :endTime")
    suspend fun getCategoryEmissionInRange(category: String, startTime: Long, endTime: Long): Double?

    @Query("DELETE FROM carbon_activities WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM carbon_activities ORDER BY date DESC")
    suspend fun getAllActivitiesList(): List<CarbonActivity>
}