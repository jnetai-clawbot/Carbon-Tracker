package com.jnetai.carbontracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnetai.carbontracker.data.dao.CarbonActivityDao
import com.jnetai.carbontracker.data.dao.GoalDao
import com.jnetai.carbontracker.data.entity.CarbonActivity
import com.jnetai.carbontracker.data.entity.Goal

@Database(entities = [CarbonActivity::class, Goal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carbonActivityDao(): CarbonActivityDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "carbon_tracker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}