package com.castillo.exploradorcontinental.model

//No se realizan peticiones para las regiones porque es innecesario
data class Region(
    val apiName: String,
    val displayName: String,
    val emoji: String
)

object Regions {
    val all = listOf(
        Region("Africa", "África", "☠️"),
        Region("Americas", "América", "🤑"),
        Region("Asia", "Asia", "🌏"),
        Region("Europe", "Europa", "🏰"),
        Region("Oceania", "Oceanía", "🏝️"),
        Region("Antarctic", "Antártida", "🧊")
    )
}