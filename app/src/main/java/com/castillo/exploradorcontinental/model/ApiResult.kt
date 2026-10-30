package com.castillo.exploradorcontinental.model

// Clase con solo dos formas posibles de representar un resultado
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: AppError) : ApiResult<Nothing>()
}

enum class ErrorType {
    NO_CONNECTION, TIMEOUT, INVALID_CREDENTIALS, RATE_LIMIT,
    NOT_FOUND, NO_CITIES, SERVER, UNKNOWN
}

data class AppError(val type: ErrorType, val message: String)