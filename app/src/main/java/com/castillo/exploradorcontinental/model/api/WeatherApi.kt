package com.castillo.exploradorcontinental.model.api

import com.castillo.exploradorcontinental.BuildConfig
import com.castillo.exploradorcontinental.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("lang") lang: String = "es",
        @Query("key") key: String = BuildConfig.WEATHER_KEY
    ): WeatherResponse
}