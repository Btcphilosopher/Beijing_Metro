package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.MetroRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class MetroViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MetroDatabase.getDatabase(application)
    private val repository = MetroRepository(database.metroDao())

    // Observe DB states
    val savedStations = repository.savedStations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savedRoutes = repository.savedRoutes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tripHistory = repository.tripHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userSettingsFlow = repository.userSettings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Local UI states
    private val _currentLanguage = MutableStateFlow("ZH") // "ZH" or "EN"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _homeStationId = MutableStateFlow("")
    val homeStationId: StateFlow<String> = _homeStationId.asStateFlow()

    private val _officeStationId = MutableStateFlow("")
    val officeStationId: StateFlow<String> = _officeStationId.asStateFlow()

    // Route Selection
    val departStationId = MutableStateFlow("")
    val arriveStationId = MutableStateFlow("")
    val currentRoutePlan = MutableStateFlow<PlanResult?>(null)

    // Active screen state
    val currentScreen = MutableStateFlow(MetroScreen.HOME)

    // Selected station for detail sheet
    val selectedStation = MutableStateFlow<Station?>(null)

    // Ticket Balance
    val ticketBalance = MutableStateFlow(85.0)

    // Ride QR code string
    private val _rideQrCode = MutableStateFlow("BJMETRO-RIDE-000000000")
    val rideQrCode: StateFlow<String> = _rideQrCode.asStateFlow()

    private val _qrCountdown = MutableStateFlow(5)
    val qrCountdown: StateFlow<Int> = _qrCountdown.asStateFlow()

    // Admin Console Mock states
    val adminAnnouncements = MutableStateFlow(BeijingMetroData.announcements)
    val liveCongestion = MutableStateFlow<Map<String, Float>>(BeijingMetroData.stations.mapValues { it.value.congestionIndex })

    init {
        // Collect DB settings and apply
        viewModelScope.launch {
            userSettingsFlow.collect { settings ->
                if (settings != null) {
                    _currentLanguage.value = settings.language
                    _isDarkMode.value = settings.isDarkMode
                    _notificationEnabled.value = settings.notificationEnabled
                    _homeStationId.value = settings.homeStationId
                    _officeStationId.value = settings.officeStationId
                } else {
                    // Pre-fill default
                    val defaultSettings = UserSettingsEntity(
                        id = 1,
                        language = "ZH",
                        isDarkMode = false,
                        notificationEnabled = true
                    )
                    repository.saveSettings(defaultSettings)
                }
            }
        }

        // Loop QR code refresh every 5s
        viewModelScope.launch {
            while (true) {
                _qrCountdown.value = 5
                while (_qrCountdown.value > 0) {
                    delay(1000)
                    _qrCountdown.value--
                }
                val randomNum = Random.nextInt(10000000, 99999999)
                val signature = Random.nextInt(100, 999)
                _rideQrCode.value = "BJMETRO-RIDE-${randomNum}-${signature}"
            }
        }
    }

    // Toggle Language
    fun toggleLanguage() {
        val newLang = if (_currentLanguage.value == "ZH") "EN" else "ZH"
        updateSettings(_homeStationId.value, _officeStationId.value, newLang, _isDarkMode.value, _notificationEnabled.value)
    }

    // Toggle Dark Mode
    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        updateSettings(_homeStationId.value, _officeStationId.value, _currentLanguage.value, newMode, _notificationEnabled.value)
    }

    // Update settings helper
    fun updateSettings(home: String, office: String, lang: String, dark: Boolean, notify: Boolean) {
        viewModelScope.launch {
            repository.saveSettings(UserSettingsEntity(
                id = 1,
                homeStationId = home,
                officeStationId = office,
                language = lang,
                isDarkMode = dark,
                notificationEnabled = notify
            ))
        }
    }

    // Set Commute addresses
    fun setHomeStation(stationId: String) {
        updateSettings(stationId, _officeStationId.value, _currentLanguage.value, _isDarkMode.value, _notificationEnabled.value)
    }

    fun setOfficeStation(stationId: String) {
        updateSettings(_homeStationId.value, stationId, _currentLanguage.value, _isDarkMode.value, _notificationEnabled.value)
    }

    // Toggle Station Favorite
    fun toggleStationFavorite(stationId: String) {
        viewModelScope.launch {
            val isFav = savedStations.value.any { it.stationId == stationId }
            if (isFav) {
                repository.removeStation(stationId)
            } else {
                repository.saveStation(stationId)
            }
        }
    }

    // Toggle Route Favorite
    fun toggleRouteFavorite(startId: String, endId: String) {
        viewModelScope.launch {
            val key = "${startId}_to_${endId}"
            val isFav = savedRoutes.value.any { it.routeKey == key }
            if (isFav) {
                repository.removeRoute(startId, endId)
            } else {
                repository.saveRoute(startId, endId)
            }
        }
    }

    // Calculate Route
    fun calculateRoute(startId: String, endId: String) {
        if (startId.isEmpty() || endId.isEmpty()) {
            currentRoutePlan.value = null
            return
        }
        val plan = BeijingMetroData.planRoute(startId, endId)
        currentRoutePlan.value = plan
        if (plan != null) {
            // Save to trip history
            viewModelScope.launch {
                repository.insertTrip(startId, endId, plan.ticketPrice, plan.totalTimeMin)
            }
        }
    }

    // Swap Depart/Arrive
    fun swapDepartArrive() {
        val temp = departStationId.value
        departStationId.value = arriveStationId.value
        arriveStationId.value = temp
        calculateRoute(departStationId.value, arriveStationId.value)
    }

    // Use Quick Ticket ride (updates balance)
    fun tapRideScan(fare: Int) {
        if (ticketBalance.value >= fare) {
            ticketBalance.value -= fare
        } else {
            // auto top up or reset
            ticketBalance.value += 50.0
        }
    }

    // Top up balance
    fun topUp(amount: Double) {
        ticketBalance.value += amount
    }

    // Add administrative announcement (Admin console)
    fun addAdminAnnouncement(zh: String, en: String, urgent: Boolean) {
        val newAd = BeijingMetroData.Advisory(
            id = (adminAnnouncements.value.size + 1).toString(),
            contentZh = zh,
            contentEn = en,
            time = "2026-07-08 10:00",
            isUrgent = urgent
        )
        adminAnnouncements.value = listOf(newAd) + adminAnnouncements.value
    }

    // Update line / station congestion (Admin console)
    fun updateStationCongestion(stationId: String, index: Float) {
        val mutable = liveCongestion.value.toMutableMap()
        mutable[stationId] = index
        liveCongestion.value = mutable
    }
}

enum class MetroScreen {
    HOME,          // 首页
    ROUTE_PLANNER, // 智能路线规划
    METRO_MAP,     // 交互线路图
    RIDE_CODE,     // 扫码乘车 / 电子乘车码
    TOURIST_GUIDE, // 游客模式
    USER_CENTER,   // 用户中心
    HELP_CENTER,   // 客服中心
    ADMIN_CONSOLE  // 管理后台
}
