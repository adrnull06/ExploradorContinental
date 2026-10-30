package com.castillo.exploradorcontinental.model

import com.google.gson.annotations.SerializedName

data class CountriesResponse(val data: CountriesData?)

data class CountriesData(
    val objects: List<Country>?,
    val meta: Meta?
)

data class Meta(
    val total: Int?,
    val count: Int?,
    val limit: Int?,
    val offset: Int?,
    val more: Boolean?
)

data class Country(
    val names: CountryNames?,
    val codes: CountryCodes?,
    val capitals: List<Capital>?,
    val flag: Flag?,
    val region: String?,
    val subregion: String?,
    val area: Area?,
    val population: Long?,
    val currencies: List<Currency>?,
    val languages: List<Language>?,
    val coordinates: Coordinates?,
    val borders: List<String>?,
    val timezones: List<String>?,
    val continents: List<String>?,
    val landlocked: Boolean?,
    val links: Links?
) {
    val displayName: String
        get() = names?.translations?.get("spa")?.common ?: names?.common ?: "Sin nombre"

    val displayOfficialName: String
        get() = names?.translations?.get("spa")?.official ?: names?.official ?: "Sin nombre oficial"

    val primaryCapital: Capital?
        get() = capitals?.firstOrNull { it.attributes?.primary == true } ?: capitals?.firstOrNull()
}

data class CountryNames(
    val common: String?,
    val official: String?,
    val translations: Map<String, TranslatedName>?
)

data class TranslatedName(val common: String?, val official: String?)

data class CountryCodes(
    @SerializedName("alpha_2") val alpha2: String?,
    @SerializedName("alpha_3") val alpha3: String?
)

data class Capital(
    val name: String?,
    val coordinates: Coordinates?,
    val attributes: CapitalAttributes?
)

data class CapitalAttributes(val primary: Boolean?)

data class Coordinates(val lat: Double?, val lng: Double?)

data class Flag(
    val emoji: String?,
    @SerializedName("url_png") val urlPng: String?,
    val colors: FlagColors?
)

data class FlagColors(
    val dominant: String?,
    val prominent: String?,
    val swatches: Map<String, String?>?
)

data class Area(val kilometers: Double?)

data class Currency(val code: String?, val name: String?, val symbol: String?)

data class Language(
    val name: String?,
    @SerializedName("native_name") val nativeName: String?
)

data class Links(
    @SerializedName("google_maps") val googleMaps: String?,
    val wikipedia: String?
)



