package com.noahjiang.skybrief.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComfortCalculatorTest {
    @Test
    fun idealWeatherProducesHighComfortScore() {
        val score = ComfortCalculator.calculateScore(
            temperatureCelsius = 24.0,
            humidity = 55,
            windSpeedKmh = 10.0,
            precipitationProbability = 0,
            uvIndex = 3.0
        )

        assertTrue(score >= 95)
    }

    @Test
    fun heavyRainOverridesComfortLevel() {
        val level = ComfortCalculator.classify(
            score = 90,
            precipitationProbability = 85,
            uvIndex = 3.0,
            windSpeedKmh = 10.0
        )

        assertEquals(ComfortLevel.POOR, level)
    }

    @Test
    fun moderateWeatherMapsToGoodDecision() {
        val score = ComfortCalculator.calculateScore(
            temperatureCelsius = 27.0,
            humidity = 65,
            windSpeedKmh = 14.0,
            precipitationProbability = 20,
            uvIndex = 5.0
        )
        val level = ComfortCalculator.classify(
            score = score,
            precipitationProbability = 20,
            uvIndex = 5.0,
            windSpeedKmh = 14.0
        )

        assertEquals(ComfortLevel.GOOD, level)
    }
}
