package com.noahjiang.skybrief.data.remote

import com.squareup.moshi.Json

data class ForecastResponse(
    val current: CurrentWeather? = null,
    val daily: DailyForecast? = null,
    val timezone: String? = null
)

data class CurrentWeather(
    val time: String? = null,
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val humidity: Int? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Double? = null
)

data class DailyForecast(
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int?>? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double?>? = null
)
