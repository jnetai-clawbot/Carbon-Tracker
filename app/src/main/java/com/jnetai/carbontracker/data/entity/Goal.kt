package com.jnetai.carbontracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,              // e.g., "Reduce transport emissions"
    val targetEmission: Double,    // kg CO2 target per period
    val period: String,            // daily, weekly, monthly
    val category: String,          // transport, food, energy, overall
    val createdDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)