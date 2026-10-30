package com.castillo.exploradorcontinental.model.api

import com.castillo.exploradorcontinental.BuildConfig
import com.castillo.exploradorcontinental.model.GeoNamesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesApi {

    @GET("searchJSON")
    suspend fun getTopCities(
        @Query("country") countryCode: String,
        @Query("featureClass") featureClass: String = "P",
        @Query("cities") cities: String = "cities15000",
        @Query("maxRows") maxRows: Int = 10,
        @Query("orderby") orderBy: String = "population",
        @Query("username") username: String = BuildConfig.GEONAMES_USER
    ): GeoNamesResponse
}