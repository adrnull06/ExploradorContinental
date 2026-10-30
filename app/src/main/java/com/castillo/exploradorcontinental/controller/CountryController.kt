package com.castillo.exploradorcontinental.controller

import com.castillo.exploradorcontinental.model.ApiResult
import com.castillo.exploradorcontinental.model.AppError
import com.castillo.exploradorcontinental.model.Country
import com.castillo.exploradorcontinental.model.ErrorType
import com.castillo.exploradorcontinental.model.api.ApiClient

class CountryController {

    private val api = ApiClient.restCountries

    suspend fun getCountriesByRegion(region: String): ApiResult<List<Country>> =
        safeCall {
            api.getCountriesByRegion(region)
                .data?.objects.orEmpty()
                .sortedBy { it.displayName }
        }

    suspend fun getCountryDetail(code: String): ApiResult<Country> =
        when (val result = safeCall { api.getCountryByCode(code).data?.objects?.firstOrNull() }) {
            is ApiResult.Success -> result.data?.let { ApiResult.Success(it) }
                ?: ApiResult.Error(AppError(ErrorType.NOT_FOUND, "No se encontró información de este país."))
            is ApiResult.Error -> result
        }
}