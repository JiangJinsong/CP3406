package com.noahjiang.skybrief.data.repository

import com.noahjiang.skybrief.data.remote.WeatherApiService
import com.noahjiang.skybrief.domain.ComfortCalculator
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override suspend fun fetchDashboard(cityName: String, unit: TemperatureUnit): WeatherDashboard {
        val trimmedCity = cityName.trim()
        require(trimmedCity.isNotBlank()) { "City name cannot be blank." }

        val location = apiService.searchCity(trimmedCity)
            .results
            .firstOrNull()
            ?: error("No location found for '$trimmedCity'. Try a larger nearby city.")

        val forecast = apiService.getForecast(
            latitude = location.latitude,
            longitude = location.longitude
        )
        val current = forecast.current ?: error("Weather data is currently unavailable.")

        val temperatureCelsius = current.temperature ?: 0.0
        val apparentCelsius = current.apparentTemperature ?: temperatureCelsius
        val humidity = current.humidity ?: 0
        val windSpeed = current.windSpeed ?: 0.0
        val rainChance = forecast.daily?.precipitationProbabilityMax?.firstOrNull() ?: 0
        val uvIndex = forecast.daily?.uvIndexMax?.firstOrNull() ?: 0.0
        val score = ComfortCalculator.calculateScore(
            temperatureCelsius = temperatureCelsius,
            humidity = humidity,
            windSpeedKmh = windSpeed,
            precipitationProbability = rainChance,
            uvIndex = uvIndex
        )
        val level = ComfortCalculator.classify(
            score = score,
            precipitationProbability = rainChance,
            uvIndex = uvIndex,
            windSpeedKmh = windSpeed
        )

        return WeatherDashboard(
            locationName = location.displayName,
            temperature = convertTemperature(temperatureCelsius, unit),
            apparentTemperature = convertTemperature(apparentCelsius, unit),
            temperatureUnit = unit,
            humidity = humidity,
            windSpeedKmh = windSpeed,
            precipitationProbability = rainChance,
            uvIndex = uvIndex,
            condition = mapWeatherCode(current.weatherCode ?: 0),
            comfortScore = score,
            comfortLevel = level,
            advice = ComfortCalculator.buildAdvice(
                level = level,
                precipitationProbability = rainChance,
                uvIndex = uvIndex,
                windSpeedKmh = windSpeed
            ),
            updatedAt = current.time ?: "Live update"
        )
    }

    private fun convertTemperature(valueCelsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> valueCelsius
            TemperatureUnit.FAHRENHEIT -> valueCelsius * 9 / 5 + 32
        }
    }

    private fun mapWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Foggy"
            51, 53, 55, 56, 57 -> "Drizzle"
            61, 63, 65, 66, 67 -> "Rain"
            71, 73, 75, 77 -> "Snow"
            80, 81, 82 -> "Rain showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Variable conditions"
        }
    }
}
