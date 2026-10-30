package com.castillo.exploradorcontinental.controller

import com.castillo.exploradorcontinental.model.ApiResult
import com.castillo.exploradorcontinental.model.AppError
import com.castillo.exploradorcontinental.model.ErrorType
import com.castillo.exploradorcontinental.model.GeoStatus
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

//Envuelva las llamadas para success o Error. Reutilizable
suspend fun <T> safeCall(block: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(ErrorHandler.fromException(e))
    }

object ErrorHandler {

    fun fromException(e: Exception): AppError = when (e) {
        is SocketTimeoutException -> AppError(
            ErrorType.TIMEOUT, "El servidor tardó demasiado en responder. Inténtalo de nuevo."
        )
        is UnknownHostException, is ConnectException -> AppError(
            ErrorType.NO_CONNECTION, "Sin conexión a internet. Revisa tu red e inténtalo de nuevo."
        )
        is HttpException -> fromHttpCode(e.code())
        is IOException -> AppError(
            ErrorType.NO_CONNECTION, "Se perdió la conexión con el servidor."
        )
        is JsonParseException -> AppError(
            ErrorType.SERVER, "El servidor devolvió una respuesta inesperada."
        )
        else -> AppError(ErrorType.UNKNOWN, "Ocurrió un error inesperado.")
    }

    private fun fromHttpCode(code: Int): AppError = when (code) {
        400 -> AppError(ErrorType.NOT_FOUND, "No se encontró la ubicación solicitada.")
        401, 403 -> AppError(
            ErrorType.INVALID_CREDENTIALS, "La clave o token de acceso no es válido, expiró o agotó su cuota."
        )
        404 -> AppError(ErrorType.NOT_FOUND, "No se encontró la información solicitada.")
        429 -> AppError(
            ErrorType.RATE_LIMIT, "Se alcanzó el límite de solicitudes. Inténtalo más tarde."
        )
        in 500..599 -> AppError(ErrorType.SERVER, "El servidor tiene problemas. Inténtalo más tarde.")
        else -> AppError(ErrorType.UNKNOWN, "Error del servidor (código $code).")
    }

    fun fromGeoStatus(status: GeoStatus): AppError = when (status.value) {
        10 -> AppError(ErrorType.INVALID_CREDENTIALS, "El usuario de GeoNames no es válido o no tiene los servicios web habilitados.")
        18, 19, 20 -> AppError(ErrorType.RATE_LIMIT, "Se agotó el límite de solicitudes de GeoNames. Inténtalo más tarde.")
        else -> AppError(ErrorType.SERVER, "GeoNames reportó un problema: ${status.message ?: "desconocido"}")
    }
}