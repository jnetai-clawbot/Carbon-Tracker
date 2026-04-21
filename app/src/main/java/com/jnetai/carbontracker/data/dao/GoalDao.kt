package com.jnetai.carbontracker.data.dao

import androidx.room.*
import com.jnetai.carbontracker.data.entity.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdDate DESC")
    fun getActiveGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdDate DESC")
    suspend fun getActiveGoalsList(): List<Goal>

    @Query("SELECT * FROM goals ORDER BY createdDate DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: String)
}