package com.noahjiang.skybrief.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noahjiang.skybrief.data.repository.TemperatureUnit
import com.noahjiang.skybrief.data.repository.WeatherDashboard
import com.noahjiang.skybrief.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class WeatherUiState(
    val cityName: String = "Singapore",
    val cityInput: String = "Singapore",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val showDetailedMetrics: Boolean = true,
    val isLoading: Boolean = false,
    val dashboard: WeatherDashboard? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        refreshWeather()
    }

    fun onCityInputChanged(value: String) {
        _uiState.update { it.copy(cityInput = value) }
    }

    fun onTemperatureUnitChanged(unit: TemperatureUnit) {
        if (_uiState.value.temperatureUnit == unit) return
        _uiState.update { it.copy(temperatureUnit = unit) }
        refreshWeather()
    }

    fun onShowDetailedMetricsChanged(show: Boolean) {
        _uiState.update { it.copy(showDetailedMetrics = show) }
    }

    fun applyCitySetting() {
        val newCity = _uiState.value.cityInput.trim()
        if (newCity.isBlank()) {
            _uiState.update { it.copy(errorMessage = "City name cannot be blank.") }
            return
        }
        _uiState.update { it.copy(cityName = newCity) }
        refreshWeather()
    }

    fun refreshWeather() {
        val requestState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.fetchDashboard(
                    cityName = requestState.cityName,
                    unit = requestState.temperatureUnit
                )
            }.onSuccess { dashboard ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dashboard = dashboard,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toUserMessage()
                    )
                }
            }
        }
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is IOException -> "Network error. Check your internet connection and try again."
            is IllegalArgumentException -> message ?: "Invalid setting value."
            else -> message ?: "Unable to load weather data."
        }
    }
}
