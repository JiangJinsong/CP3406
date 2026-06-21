package com.noahjiang.skybrief.domain

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Pure domain logic for the main utility decision.
 * Keeping this calculation outside the UI and repository makes it easy to test and explain.
 */
object ComfortCalculator {
    fun calculateScore(
        temperatureCelsius: Double,
        humidity: Int,
        windSpeedKmh: Double,
        precipitationProbability: Int,
        uvIndex: Double
    ): Int {
        val temperaturePenalty = abs(temperatureCelsius - IDEAL_TEMPERATURE_CELSIUS) * TEMPERATURE_WEIGHT
        val humidityPenalty = abs(humidity - IDEAL_HUMIDITY_PERCENT) * HUMIDITY_WEIGHT
        val windPenalty = if (windSpeedKmh > WIND_THRESHOLD_KMH) {
            (windSpeedKmh - WIND_THRESHOLD_KMH) * WIND_WEIGHT
        } else {
            0.0
        }
        val rainPenalty = precipitationProbability * RAIN_WEIGHT
        val uvPenalty = if (uvIndex > UV_THRESHOLD) {
            (uvIndex - UV_THRESHOLD) * UV_WEIGHT
        } else {
            0.0
        }

        return (100 - temperaturePenalty - humidityPenalty - windPenalty - rainPenalty - uvPenalty)
            .roundToInt()
            .coerceIn(0, 100)
    }

    fun classify(
        score: Int,
        precipitationProbability: Int,
        uvIndex: Double,
        windSpeedKmh: Double
    ): ComfortLevel {
        return when {
            precipitationProbability >= HEAVY_RAIN_PROBABILITY || windSpeedKmh >= STRONG_WIND_KMH -> ComfortLevel.POOR
            uvIndex >= HIGH_UV_INDEX || precipitationProbability >= MODERATE_RAIN_PROBABILITY -> ComfortLevel.CAUTION
            score >= EXCELLENT_SCORE -> ComfortLevel.EXCELLENT
            score >= GOOD_SCORE -> ComfortLevel.GOOD
            score >= CAUTION_SCORE -> ComfortLevel.CAUTION
            else -> ComfortLevel.POOR
        }
    }

    fun buildAdvice(
        level: ComfortLevel,
        precipitationProbability: Int,
        uvIndex: Double,
        windSpeedKmh: Double
    ): String {
        return when {
            precipitationProbability >= HEAVY_RAIN_PROBABILITY -> "High rain risk. Carry an umbrella and avoid unnecessary outdoor travel."
            windSpeedKmh >= STRONG_WIND_KMH -> "Strong wind is likely to reduce comfort. Secure loose items before leaving."
            uvIndex >= HIGH_UV_INDEX -> "Very high UV. Use sun protection and keep outdoor time short."
            level == ComfortLevel.EXCELLENT -> "Good conditions for walking, commuting, or outdoor study breaks."
            level == ComfortLevel.GOOD -> "Generally suitable for outdoor plans, with normal preparation."
            level == ComfortLevel.CAUTION -> "Usable conditions, but check rain, heat, wind, and UV before going out."
            else -> "Plan indoor activities or prepare carefully before leaving."
        }
    }

    private const val IDEAL_TEMPERATURE_CELSIUS = 24.0
    private const val IDEAL_HUMIDITY_PERCENT = 55
    private const val WIND_THRESHOLD_KMH = 22.0
    private const val UV_THRESHOLD = 8.0

    private const val TEMPERATURE_WEIGHT = 3.2
    private const val HUMIDITY_WEIGHT = 0.35
    private const val WIND_WEIGHT = 1.3
    private const val RAIN_WEIGHT = 0.25
    private const val UV_WEIGHT = 4.0

    private const val EXCELLENT_SCORE = 85
    private const val GOOD_SCORE = 70
    private const val CAUTION_SCORE = 50
    private const val MODERATE_RAIN_PROBABILITY = 55
    private const val HEAVY_RAIN_PROBABILITY = 80
    private const val HIGH_UV_INDEX = 9.0
    private const val STRONG_WIND_KMH = 40.0
}

enum class ComfortLevel(
    val label: String,
    val decision: String
) {
    EXCELLENT("Excellent", "Go outside"),
    GOOD("Good", "Suitable"),
    CAUTION("Caution", "Prepare first"),
    POOR("Poor", "Stay indoors")
}
