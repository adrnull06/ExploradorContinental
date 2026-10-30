package com.castillo.exploradorcontinental.model

//Clase que representa los datos que recibimos desde la API de GeoNames

data class GeoNamesResponse(
    val geonames: List<GeoCity>?,
    val status: GeoStatus?
)

data class GeoCity(
    val geonameId: Long,
    val name: String,
    val lat: String,
    val lng: String,
    val population: Long?,
    val fcode: String?,
    val countryCode: String?
) {
    val isCapital: Boolean get() = fcode == "PPLC" //Asi marca GeoNames una capital
}

data class GeoStatus(
    val message: String?,
    val value: Int?
)