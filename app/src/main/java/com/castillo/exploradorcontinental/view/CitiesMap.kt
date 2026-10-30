package com.castillo.exploradorcontinental.view

import com.castillo.exploradorcontinental.model.GeoCity
import com.castillo.exploradorcontinental.model.WeatherState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CitiesMap(private val mapView: MapView) {

    private val markers = mutableMapOf<Long, Marker>()

    init {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.minZoomLevel = 2.0
        mapView.controller.setZoom(4.0)
        mapView.overlays.add(CopyrightOverlay(mapView.context))
    }

    fun showCities(cities: List<GeoCity>) {
        clearMarkers()
        val points = mutableListOf<GeoPoint>()

        cities.forEach { city ->
            val point = toGeoPoint(city) ?: return@forEach
            val marker = Marker(mapView).apply {
                position = point
                title = if (city.isCapital) "⭐ ${city.name}" else city.name
                snippet = "Cargando clima…"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(marker)
            markers[city.geonameId] = marker
            points.add(point)
        }

        fitToPoints(points)
        mapView.invalidate()
    }

    fun updateWeather(geonameId: Long, state: WeatherState) {
        val marker = markers[geonameId] ?: return
        marker.snippet = when (state) {
            is WeatherState.Loaded -> {
                val w = state.weather
                val temp = w.tempC?.let { String.format("%.1f °C", it) } ?: "—"
                "$temp · ${w.condition?.text ?: "Sin descripción"}"
            }
            is WeatherState.Failed -> "Clima no disponible"
            WeatherState.Loading -> "Cargando clima…"
        }
        if (marker.isInfoWindowOpen) marker.showInfoWindow()
    }

    fun centerOn(city: GeoCity): Boolean {
        val marker = markers[city.geonameId] ?: return false
        InfoWindow.closeAllInfoWindowsOn(mapView)
        mapView.controller.animateTo(marker.position, 11.0, 800L)
        marker.showInfoWindow()
        return true
    }

    private fun clearMarkers() {
        mapView.overlays.removeAll(markers.values.toSet())
        markers.clear()
    }

    private fun toGeoPoint(city: GeoCity): GeoPoint? {
        val lat = city.lat.toDoubleOrNull() ?: return null
        val lng = city.lng.toDoubleOrNull() ?: return null
        return GeoPoint(lat, lng)
    }

    private fun fitToPoints(points: List<GeoPoint>) {
        when {
            points.isEmpty() -> Unit
            points.size == 1 -> {
                mapView.controller.setZoom(9.0)
                mapView.controller.setCenter(points[0])
            }
            else -> mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 80)
        }
    }
}