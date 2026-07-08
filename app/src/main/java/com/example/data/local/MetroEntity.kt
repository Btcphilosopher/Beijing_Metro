package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_stations")
data class SavedStationEntity(
    @PrimaryKey val stationId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_routes")
data class SavedRouteEntity(
    @PrimaryKey val routeKey: String, // format "startId_to_endId"
    val startStationId: String,
    val endStationId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trip_history")
data class TripHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startStationId: String,
    val endStationId: String,
    val price: Int,
    val durationMin: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1, // Only 1 settings row
    val homeStationId: String = "",
    val officeStationId: String = "",
    val language: String = "ZH", // "ZH" or "EN"
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true
)
