package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetroDao {

    // Saved Stations
    @Query("SELECT * FROM saved_stations ORDER BY timestamp DESC")
    fun getSavedStations(): Flow<List<SavedStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedStation(station: SavedStationEntity)

    @Query("DELETE FROM saved_stations WHERE stationId = :stationId")
    suspend fun deleteSavedStation(stationId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_stations WHERE stationId = :stationId LIMIT 1)")
    fun isStationSaved(stationId: String): Flow<Boolean>

    // Saved Routes
    @Query("SELECT * FROM saved_routes ORDER BY timestamp DESC")
    fun getSavedRoutes(): Flow<List<SavedRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRoute(route: SavedRouteEntity)

    @Query("DELETE FROM saved_routes WHERE routeKey = :routeKey")
    suspend fun deleteSavedRoute(routeKey: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_routes WHERE routeKey = :routeKey LIMIT 1)")
    fun isRouteSaved(routeKey: String): Flow<Boolean>

    // Trip History
    @Query("SELECT * FROM trip_history ORDER BY timestamp DESC LIMIT 50")
    fun getTripHistory(): Flow<List<TripHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripHistory(trip: TripHistoryEntity)

    @Query("DELETE FROM trip_history")
    suspend fun clearTripHistory()

    // User Settings
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettingsEntity)
}
