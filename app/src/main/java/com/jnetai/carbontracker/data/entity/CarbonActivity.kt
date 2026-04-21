package com.jnetai.carbontracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "carbon_activities")
data class CarbonActivity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val category: String,       // transport, food, energy
    val subcategory: String,   // car, bus, train, flight, meat, veggie, vegan, electricity, gas, heating
    val value: Double,         // km for transport, meals for food, kWh for energy
    val emission: Double,      // calculated CO2 in kg
    val date: Long,            // timestamp in millis
    val notes: String = ""
)