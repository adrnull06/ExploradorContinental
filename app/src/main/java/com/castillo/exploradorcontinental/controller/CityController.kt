package com.castillo.exploradorcontinental.controller

import com.castillo.exploradorcontinental.model.ApiResult
import com.castillo.exploradorcontinental.model.AppError
import com.castillo.exploradorcontinental.model.ErrorType
import com.castillo.exploradorcontinental.model.GeoCity
import com.castillo.exploradorcontinental.model.api.ApiClient

class CityController {

    private val api = ApiClient.geoNames

    suspend fun getTopCities(countryCode: String): ApiResult<List<GeoCity>> =
        when (val result = safeCall { api.getTopCities(countryCode) }) {
            is ApiResult.Success -> {
                val status = result.data.status
                val cities = result.data.geonames.orEmpty()
                when {
                    status != null -> ApiResult.Error(ErrorHandler.fromGeoStatus(status))
                    cities.isEmpty() -> ApiResult.Error(
                        AppError(ErrorType.NO_CITIES, "No se encontraron ciudades principales para este país.")
                    )
                    else -> ApiResult.Success(
                        cities.sortedWith(
                            compareByDescending<GeoCity> { it.isCapital }
                                .thenByDescending { it.population ?: 0L }
                        )
                    )
                }
            }
            is ApiResult.Error -> result
        }
}