package com.jnetai.carbontracker.data.repository

import com.jnetai.carbontracker.data.dao.GoalDao
import com.jnetai.carbontracker.data.entity.Goal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GoalRepository(private val dao: GoalDao) {

    fun getActiveGoals(): Flow<List<Goal>> = dao.getActiveGoals()

    suspend fun insert(goal: Goal) = withContext(Dispatchers.IO) {
        dao.insert(goal)
    }

    suspend fun delete(goal: Goal) = withContext(Dispatchers.IO) {
        dao.delete(goal)
    }

    suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun getActiveGoalsList(): List<Goal> = withContext(Dispatchers.IO) {
        dao.getActiveGoalsList()
    }
}