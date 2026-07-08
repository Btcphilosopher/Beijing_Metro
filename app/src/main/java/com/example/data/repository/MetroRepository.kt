package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class MetroRepository(private val metroDao: MetroDao) {
    val savedStations: Flow<List<SavedStationEntity>> = metroDao.getSavedStations()
    val savedRoutes: Flow<List<SavedRouteEntity>> = metroDao.getSavedRoutes()
    val tripHistory: Flow<List<TripHistoryEntity>> = metroDao.getTripHistory()
    val userSettings: Flow<UserSettingsEntity?> = metroDao.getUserSettings()

    suspend fun saveStation(stationId: String) {
        metroDao.insertSavedStation(SavedStationEntity(stationId))
    }

    suspend fun removeStation(stationId: String) {
        metroDao.deleteSavedStation(stationId)
    }

    fun isStationSaved(stationId: String): Flow<Boolean> {
        return metroDao.isStationSaved(stationId)
    }

    suspend fun saveRoute(startStationId: String, endStationId: String) {
        val key = "${startStationId}_to_${endStationId}"
        metroDao.insertSavedRoute(SavedRouteEntity(key, startStationId, endStationId))
    }

    suspend fun removeRoute(startStationId: String, endStationId: String) {
        val key = "${startStationId}_to_${endStationId}"
        metroDao.deleteSavedRoute(key)
    }

    fun isRouteSaved(startStationId: String, endStationId: String): Flow<Boolean> {
        val key = "${startStationId}_to_${endStationId}"
        return metroDao.isRouteSaved(key)
    }

    suspend fun insertTrip(startStationId: String, endStationId: String, price: Int, durationMin: Int) {
        metroDao.insertTripHistory(TripHistoryEntity(
            startStationId = startStationId,
            endStationId = endStationId,
            price = price,
            durationMin = durationMin
        ))
    }

    suspend fun clearHistory() {
        metroDao.clearTripHistory()
    }

    suspend fun saveSettings(settings: UserSettingsEntity) {
        metroDao.saveUserSettings(settings)
    }
}
