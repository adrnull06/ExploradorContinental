package com.castillo.exploradorcontinental.model

sealed class WeatherState {
    object Loading : WeatherState()
    data class Loaded(val weather: CurrentWeather) : WeatherState()
    data class Failed(val message: String) : WeatherState()
}

data class CityWeather(
    val city: GeoCity,
    val state: WeatherState = WeatherState.Loading
)