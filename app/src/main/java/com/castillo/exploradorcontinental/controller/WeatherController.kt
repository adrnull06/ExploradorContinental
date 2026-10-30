package com.castillo.exploradorcontinental.controller

import com.castillo.exploradorcontinental.model.ApiResult
import com.castillo.exploradorcontinental.model.AppError
import com.castillo.exploradorcontinental.model.CurrentWeather
import com.castillo.exploradorcontinental.model.ErrorType
import com.castillo.exploradorcontinental.model.GeoCity
import com.castillo.exploradorcontinental.model.WeatherState
import com.castillo.exploradorcontinental.model.api.ApiClient

class WeatherController {

    private val api = ApiClient.weather

    suspend fun getCurrentWeather(lat: String, lng: String): ApiResult<CurrentWeather> =
        when (val result = safeCall { api.getCurrentWeather("$lat,$lng") }) {
            is ApiResult.Success -> result.data.current?.let { ApiResult.Success(it) }
                ?: ApiResult.Error(
                    AppError(ErrorType.NOT_FOUND, "El clima de esta ciudad no está disponible.")
                )
            is ApiResult.Error -> result
        }

    suspend fun getWeatherFor(city: GeoCity): WeatherState =
        when (val result = getCurrentWeather(city.lat, city.lng)) {
            is ApiResult.Success -> WeatherState.Loaded(result.data)
            is ApiResult.Error -> WeatherState.Failed(result.error.message)
        }
}