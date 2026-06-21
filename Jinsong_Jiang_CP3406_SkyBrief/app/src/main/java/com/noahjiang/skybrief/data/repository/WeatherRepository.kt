package com.noahjiang.skybrief.data.repository

import com.noahjiang.skybrief.domain.ComfortLevel

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

data class WeatherDashboard(
    val locationName: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val temperatureUnit: TemperatureUnit,
    val humidity: Int,
    val windSpeedKmh: Double,
    val precipitationProbability: Int,
    val uvIndex: Double,
    val condition: String,
    val comfortScore: Int,
    val comfortLevel: ComfortLevel,
    val advice: String,
    val updatedAt: String
) {
    val formattedTemperature: String
        get() = "%.1f%s".format(temperature, temperatureUnit.symbol)

    val formattedApparentTemperature: String
        get() = "%.1f%s".format(apparentTemperature, temperatureUnit.symbol)
}

interface WeatherRepository {
    suspend fun fetchDashboard(cityName: String, unit: TemperatureUnit): WeatherDashboard
}
