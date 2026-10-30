package com.castillo.exploradorcontinental.model.api

import com.castillo.exploradorcontinental.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Una instancia compartida de Retrofit para todas las llamadas
// Se evita que se cree una nueva instancia para cada llamada
object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
    }

    private val baseClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    private val restCountriesClient = baseClient.newBuilder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${BuildConfig.RC_TOKEN}")
                .build()
            chain.proceed(request)
        }
        .build()

    // Funcion para construir un Retrofit con un baseUrl y un cliente
    private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val restCountries: RestCountriesApi by lazy {
        buildRetrofit("https://api.restcountries.com/", restCountriesClient)
            .create(RestCountriesApi::class.java)
    }

    val geoNames: GeoNamesApi by lazy {
        buildRetrofit("https://secure.geonames.org/", baseClient)
            .create(GeoNamesApi::class.java)
    }

    val weather: WeatherApi by lazy {
        buildRetrofit("https://api.weatherapi.com/v1/", baseClient)
            .create(WeatherApi::class.java)
    }
}