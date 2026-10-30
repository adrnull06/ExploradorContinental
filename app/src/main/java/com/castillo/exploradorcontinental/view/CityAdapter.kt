package com.castillo.exploradorcontinental.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.castillo.exploradorcontinental.databinding.ItemCityBinding
import com.castillo.exploradorcontinental.model.CityWeather
import com.castillo.exploradorcontinental.model.WeatherState
import java.text.NumberFormat

class CityAdapter(
    private val onClick: (CityWeather) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private var items: MutableList<CityWeather> = mutableListOf()

    class ViewHolder(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root)

    fun submit(newItems: List<CityWeather>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun updateWeather(geonameId: Long, state: WeatherState) {
        val index = items.indexOfFirst { it.city.geonameId == geonameId }
        if (index >= 0) {
            items[index] = items[index].copy(state = state)
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val city = item.city
        val b = holder.binding
        val density = b.root.resources.displayMetrics.density

        b.txtCiudad.text = city.name
        b.txtPoblacion.isVisible = city.population != null
        b.txtPoblacion.text = city.population
            ?.let { "Población: ${NumberFormat.getInstance().format(it)}" }.orEmpty()

        b.txtCapitalBadge.isVisible = city.isCapital
        b.root.strokeColor = Color.parseColor("#FFB300")
        b.root.strokeWidth = if (city.isCapital) (3 * density).toInt() else 0

        val state = item.state
        b.weatherLoading.isVisible = state is WeatherState.Loading
        b.weatherContent.isVisible = state is WeatherState.Loaded
        b.txtClimaError.isVisible = state is WeatherState.Failed

        when (state) {
            is WeatherState.Loaded -> {
                val w = state.weather
                b.txtTemp.text = "${fmt(w.tempC, "°C")} · ${fmt(w.tempF, "°F")}"
                b.txtCondicion.text = w.condition?.text ?: "Sin descripción"
                b.txtDetalles.text =
                    "💨 ${fmt(w.windKph, "km/h")} (${fmt(w.windMph, "mph")})   💧 ${w.humidity ?: "—"}%"
                b.txtActualizado.text = "🕒 Actualizado: ${w.lastUpdated ?: "—"}"
                b.imgClima.load(iconUrl(w.condition?.icon))
            }
            is WeatherState.Failed -> b.txtClimaError.text = "⚠️ ${state.message}"
            WeatherState.Loading -> Unit
        }

        b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    private fun fmt(value: Double?, unit: String): String =
        value?.let { String.format("%.1f %s", it, unit) } ?: "—"

    private fun iconUrl(icon: String?): String? =
        icon?.let { if (it.startsWith("//")) "https:$it" else it }
}