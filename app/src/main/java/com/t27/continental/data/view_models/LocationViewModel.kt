package com.t27.continental.data.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.network.MashinaApi
import com.t27.continental.data.repository.LocationRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchLocationsState {
    data class Success(val locations: List<Location>) : SearchLocationsState
    data object Error : SearchLocationsState
    data object Loading : SearchLocationsState
    data object Initial : SearchLocationsState
}

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    val locationListState = locationRepository.getAllLocationsStream()
        .map { LocationState(it) }
        .stateIn(
            scope = viewModelScope,
            initialValue = LocationState(),
            started = SharingStarted.WhileSubscribed(5_000L)
        )

    private val _searchLocationState =
        MutableStateFlow<SearchLocationsState>(SearchLocationsState.Initial)
    val searchLocationState = _searchLocationState.asStateFlow()

    private val _locationQuery = MutableStateFlow("")
    val locationQuery = _locationQuery.asStateFlow()

    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    fun updateLocationQuery(q: String, source: String) {
        _locationQuery.value = q

        searchJob?.cancel()

        searchJob = locationQuery.debounce(750).onEach { query ->
            if (query.isBlank()) {
                _searchLocationState.value = SearchLocationsState.Initial
                return@onEach
            }
            searchLocation(q, source)

        }.launchIn(viewModelScope)
    }

    private fun searchLocation(q: String, source: String) {
        viewModelScope.launch {
            _searchLocationState.value = SearchLocationsState.Loading
            _searchLocationState.value = try {
                val locationResult = MashinaApi.retrofitService.searchChangeLocation(source, q)
                SearchLocationsState.Success(locationResult.locations ?: listOf())
            } catch (e: Exception) {
                SearchLocationsState.Error
            }
        }
    }

    fun insertLocation(location: Location, prevLocation: Location?) {
        viewModelScope.launch {
            if (prevLocation != null) {
                if (prevLocation.source == location.source) {
                    locationRepository.updateLocation(location)
                    return@launch
                }
            }
            locationRepository.insertLocation(location)
        }
    }

    fun getLocation(source: SearchSource): StateFlow<Location?> {
        return locationRepository.getLocationStream(source).stateIn(
            scope = viewModelScope,
            initialValue = Location(),
            started = SharingStarted.Eagerly
        )
    }
}

data class LocationState(
    val locationList: List<Location> = listOf()
)