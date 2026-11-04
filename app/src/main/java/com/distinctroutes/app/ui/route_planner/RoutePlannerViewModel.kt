package com.distinctroutes.app.ui.route_planner

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distinctroutes.app.data.local.JournalStorage
import com.distinctroutes.app.data.local.PreferencesStorage
import com.distinctroutes.app.data.repository.BikeRouteRepository
import com.distinctroutes.app.domain.model.BikeType
import com.distinctroutes.app.domain.model.FavoriteRoute
import com.distinctroutes.app.domain.model.Route
import com.distinctroutes.app.domain.model.SafetyPreferences
import com.distinctroutes.app.domain.usecase.AddressLocation
import com.distinctroutes.app.domain.usecase.CalculateRouteUseCase
import com.distinctroutes.app.domain.usecase.ExportToGPXUseCase
import com.distinctroutes.app.domain.usecase.GeocodingUseCase
import com.distinctroutes.app.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class RoutePlannerUiState {
    object Idle : RoutePlannerUiState()
    object Loading : RoutePlannerUiState()
    data class Success(val route: Route) : RoutePlannerUiState()
    data class Error(val message: String) : RoutePlannerUiState()
}

data class ViaStopData(
    val id: String = UUID.randomUUID().toString(),
    var addressText: String = "",
    var lat: Double = 0.0,
    var lon: Double = 0.0
)

@HiltViewModel
class RoutePlannerViewModel @Inject constructor(
    private val calculateRouteUseCase: CalculateRouteUseCase,
    private val geocodingUseCase: GeocodingUseCase,
    val locationHelper: LocationHelper,
    private val exportToGPXUseCase: ExportToGPXUseCase,
    private val preferencesStorage: PreferencesStorage,
    private val journalStorage: JournalStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoutePlannerUiState>(RoutePlannerUiState.Idle)
    val uiState: StateFlow<RoutePlannerUiState> = _uiState.asStateFlow()

    private val _preferences = MutableStateFlow(preferencesStorage.getSafetyPreferences())
    val preferences: StateFlow<SafetyPreferences> = _preferences.asStateFlow()

    val selectedCity: String
        get() = preferencesStorage.getDefaultCity()

    private val _isPreferencesExpanded = MutableStateFlow(false)
    val isPreferencesExpanded: StateFlow<Boolean> = _isPreferencesExpanded.asStateFlow()

    var startAddressText = MutableStateFlow("")
    var startLat: Double = -31.9505
    var startLon: Double = 115.8605

    var endAddressText = MutableStateFlow("")
    var endLat: Double = -31.9505
    var endLon: Double = 115.8605

    val viaStops = MutableStateFlow<List<ViaStopData>>(emptyList())

    val startSuggestions = MutableStateFlow<List<AddressLocation>>(emptyList())
    val endSuggestions = MutableStateFlow<List<AddressLocation>>(emptyList())
    val viaSuggestions = MutableStateFlow<List<AddressLocation>>(emptyList())

    fun togglePreferencesExpanded() {
        _isPreferencesExpanded.value = !_isPreferencesExpanded.value
    }

    fun swapStartAndEnd() {
        val tempAddr = startAddressText.value
        val tempLat = startLat
        val tempLon = startLon

        startAddressText.value = endAddressText.value
        startLat = endLat
        startLon = endLon

        endAddressText.value = tempAddr
        endLat = tempLat
        endLon = tempLon
    }

    fun addViaStop(address: String = "", lat: Double = 0.0, lon: Double = 0.0) {
        val current = viaStops.value.toMutableList()
        current.add(ViaStopData(addressText = address, lat = lat, lon = lon))
        viaStops.value = current
    }

    fun removeViaStop(id: String) {
        val current = viaStops.value.toMutableList()
        current.removeAll { it.id == id }
        viaStops.value = current
    }

    fun moveViaStopUp(index: Int) {
        if (index > 0) {
            val current = viaStops.value.toMutableList()
            val temp = current[index]
            current[index] = current[index - 1]
            current[index - 1] = temp
            viaStops.value = current
        }
    }

    fun moveViaStopDown(index: Int) {
        if (index < viaStops.value.size - 1) {
            val current = viaStops.value.toMutableList()
            val temp = current[index]
            current[index] = current[index + 1]
            current[index + 1] = temp
            viaStops.value = current
        }
    }

    fun clearAllInputs() {
        startAddressText.value = ""
        startLat = -31.9505
        startLon = 115.8605

        endAddressText.value = ""
        endLat = -31.9505
        endLon = 115.8605

        viaStops.value = emptyList()
        startSuggestions.value = emptyList()
        endSuggestions.value = emptyList()
        viaSuggestions.value = emptyList()

        _uiState.value = RoutePlannerUiState.Idle
    }

    fun updateSafetyPreferences(
        preferResidentialRoads: String = _preferences.value.preferResidentialRoads,
        useFerries: Boolean = _preferences.value.useFerries,
        preferredSpeedKmH: Int = _preferences.value.preferredSpeedKmH,
        roadSurfaceQuality: String = _preferences.value.roadSurfaceQuality,
        avoidTrafficLights: Boolean = _preferences.value.avoidTrafficLights,
        avoidHighways: Boolean = _preferences.value.avoidHighways,
        avoidSteepHills: Boolean = _preferences.value.avoidSteepHills,
        hasDogTrailer: Boolean = _preferences.value.hasDogTrailer,
        preferGreenRoutes: Boolean = _preferences.value.preferGreenRoutes,
        avoidUnlitStreets: Boolean = _preferences.value.avoidUnlitStreets,
        avoidCobblestones: Boolean = _preferences.value.avoidCobblestones,
        bikeType: BikeType = _preferences.value.bikeType
    ) {
        _preferences.update {
            it.copy(
                preferResidentialRoads = preferResidentialRoads,
                useFerries = useFerries,
                preferredSpeedKmH = preferredSpeedKmH,
                roadSurfaceQuality = roadSurfaceQuality,
                avoidTrafficLights = avoidTrafficLights,
                avoidHighways = avoidHighways,
                avoidSteepHills = avoidSteepHills,
                hasDogTrailer = hasDogTrailer,
                preferGreenRoutes = preferGreenRoutes,
                avoidUnlitStreets = avoidUnlitStreets,
                avoidCobblestones = avoidCobblestones,
                bikeType = bikeType
            )
        }
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun searchAddress(query: String, field: String) {
        if (query.length < 3) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(250)
            geocodingUseCase.searchAddress(query = query, cityContext = selectedCity).onSuccess { list ->
                when (field) {
                    "start" -> startSuggestions.value = list
                    "end" -> endSuggestions.value = list
                    else -> viaSuggestions.value = list
                }
            }
        }
    }

    fun selectAddressSuggestion(location: AddressLocation, field: String) {
        when (field) {
            "start" -> {
                startAddressText.value = location.displayName
                startLat = location.lat
                startLon = location.lon
                startSuggestions.value = emptyList()
            }
            "end" -> {
                endAddressText.value = location.displayName
                endLat = location.lat
                endLon = location.lon
                endSuggestions.value = emptyList()
            }
            else -> {
                val stop = viaStops.value.find { it.id == field }
                if (stop != null) {
                    stop.addressText = location.displayName
                    stop.lat = location.lat
                    stop.lon = location.lon
                }
                viaSuggestions.value = emptyList()
            }
        }
    }

    fun setStartFromMap(lat: Double, lon: Double) {
        startLat = lat
        startLon = lon
        viewModelScope.launch {
            geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                startAddressText.value = address
            }
        }
    }

    fun setEndFromMap(lat: Double, lon: Double) {
        endLat = lat
        endLon = lon
        viewModelScope.launch {
            geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                endAddressText.value = address
            }
        }
    }

    fun addViaFromMap(lat: Double, lon: Double) {
        viewModelScope.launch {
            geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                addViaStop(address = address, lat = lat, lon = lon)
            }
        }
    }

    fun useCurrentGpsLocation() {
        viewModelScope.launch {
            _uiState.value = RoutePlannerUiState.Loading
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                startLat = location.lat
                startLon = location.lon
                geocodingUseCase.reverseGeocode(location.lat, location.lon).onSuccess { address ->
                    startAddressText.value = address
                }
                _uiState.value = RoutePlannerUiState.Idle
            } else {
                _uiState.value = RoutePlannerUiState.Error("GPS location unavailable or permission denied")
            }
        }
    }

    fun onStartPinMoved(lat: Double, lon: Double) {
        startLat = lat
        startLon = lon
        viewModelScope.launch {
            geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                if (startAddressText.value.isBlank() || startAddressText.value.startsWith("Location near") || startAddressText.value.startsWith("Map Pin")) {
                    startAddressText.value = address
                }
            }
        }
    }

    fun onEndPinMoved(lat: Double, lon: Double) {
        endLat = lat
        endLon = lon
        viewModelScope.launch {
            geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                if (endAddressText.value.isBlank() || endAddressText.value.startsWith("Location near") || endAddressText.value.startsWith("Map Pin")) {
                    endAddressText.value = address
                }
            }
        }
    }

    fun onViaPinMoved(index: Int, lat: Double, lon: Double) {
        val currentList = viaStops.value
        if (index in currentList.indices) {
            val stop = currentList[index]
            stop.lat = lat
            stop.lon = lon
            viewModelScope.launch {
                geocodingUseCase.reverseGeocode(lat, lon).onSuccess { address ->
                    stop.addressText = address
                }
            }
        }
    }

    fun calculateRoute() {
        if (startAddressText.value.isBlank() || endAddressText.value.isBlank()) {
            _uiState.value = RoutePlannerUiState.Error("Please enter both a Start and Destination address.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RoutePlannerUiState.Loading
            val result = calculateRouteUseCase.execute(
                startLat = startLat,
                startLon = startLon,
                endLat = endLat,
                endLon = endLon,
                preferences = _preferences.value,
                viaWaypoints = viaStops.value.map { Pair(it.lat, it.lon) }
            )

            result.fold(
                onSuccess = { route ->
                    _uiState.value = RoutePlannerUiState.Success(route)
                    try {
                        val journalEntry = FavoriteRoute(
                            id = "${startAddressText.value}|${endAddressText.value}",
                            title = "${startAddressText.value.take(15)} → ${endAddressText.value.take(15)}",
                            startAddress = startAddressText.value,
                            startLat = startLat,
                            startLon = startLon,
                            endAddress = endAddressText.value,
                            endLat = endLat,
                            endLon = endLon,
                            viaAddress = if (viaStops.value.isNotEmpty()) viaStops.value.first().addressText else null,
                            viaLat = if (viaStops.value.isNotEmpty()) viaStops.value.first().lat else null,
                            viaLon = if (viaStops.value.isNotEmpty()) viaStops.value.first().lon else null,
                            hasViaPoint = viaStops.value.isNotEmpty(),
                            distanceKm = route.distanceKm,
                            durationMinutes = route.durationMinutes,
                            bikeTypeDisplayName = _preferences.value.bikeType.displayName,
                            safetySummary = route.safetySummary
                        )
                        journalStorage.logRide(journalEntry)
                    } catch (e: Exception) {
                        // Silently ignore journal errors
                    }
                },
                onFailure = { error ->
                    _uiState.value = RoutePlannerUiState.Error(error.localizedMessage ?: "Failed to calculate route")
                }
            )
        }
    }

    fun getOsmAndHandoffIntent(route: Route): Intent {
        return exportToGPXUseCase.createOsmAndIntent(route)
    }

    fun getNavigationShareIntent(route: Route): Intent {
        return exportToGPXUseCase.createNavigationShareIntent(route)
    }
}