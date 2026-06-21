package com.noahjiang.skybrief.data.remote

import com.squareup.moshi.Json

data class GeocodingResponse(
    val results: List<GeocodingResult> = emptyList()
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @Json(name = "admin1") val region: String? = null
) {
    val displayName: String
        get() = listOfNotNull(name, region, country).joinToString(", ")
}
