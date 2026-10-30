package com.castillo.exploradorcontinental.model.api

import com.castillo.exploradorcontinental.model.CountriesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//Lista de "pedidos" que se convierten luego en llamadas a la API reales con RETROFIT
interface RestCountriesApi {

    @GET("countries/v5")
    suspend fun getCountriesByRegion( // "suspend" significa ejecutarse en corrutina
        @Query("region") region: String,
        @Query("limit") limit: Int = 100,
        @Query("response_fields") fields: String =
            "names.common,names.translations.spa,capitals,flag.url_png,codes.alpha_2"
    ): CountriesResponse

    @GET("countries/v5/codes.alpha_2/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String
    ): CountriesResponse
}