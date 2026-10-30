package com.castillo.exploradorcontinental.model

import com.google.gson.annotations.SerializedName //Para convertir nombres de variables

data class WeatherResponse(
    val location: WeatherLocation?,
    val current: CurrentWeather?
)

data class WeatherLocation(
    val name: String?,
    val country: String?
)

data class CurrentWeather(
    @SerializedName("last_updated") val lastUpdated: String?,
    @SerializedName("temp_c") val tempC: Double?,
    @SerializedName("temp_f") val tempF: Double?,
    @SerializedName("wind_kph") val windKph: Double?,
    @SerializedName("wind_mph") val windMph: Double?,
    val humidity: Int?,
    val condition: WeatherCondition?
)

data class WeatherCondition(
    val text: String?,
    val icon: String?
)