package com.castillo.exploradorcontinental.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.castillo.exploradorcontinental.controller.CountryController
import com.castillo.exploradorcontinental.databinding.ActivityCountryDetailBinding
import com.castillo.exploradorcontinental.databinding.ItemInfoRowBinding
import com.castillo.exploradorcontinental.databinding.ItemSectionBinding
import com.castillo.exploradorcontinental.databinding.ItemStatBinding
import com.castillo.exploradorcontinental.model.ApiResult
import com.castillo.exploradorcontinental.model.Country
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.text.NumberFormat
import androidx.recyclerview.widget.LinearLayoutManager
import com.castillo.exploradorcontinental.controller.CityController
import com.castillo.exploradorcontinental.controller.WeatherController
import com.castillo.exploradorcontinental.model.CityWeather
import com.castillo.exploradorcontinental.model.ErrorType
import com.castillo.exploradorcontinental.model.GeoCity
import android.content.Context
import android.widget.Toast
import org.osmdroid.config.Configuration
import java.io.File

class CountryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryDetailBinding
    private val controller = CountryController()

    private val cityController = CityController()
    private val weatherController = WeatherController()
    private var citiesRequested = false
    private lateinit var citiesMap: CitiesMap
    private val cityAdapter = CityAdapter { cityWeather ->
        if (!citiesMap.centerOn(cityWeather.city)) {
            Toast.makeText(this, "No se encontró la ubicación de esta ciudad.", Toast.LENGTH_SHORT).show()
        }
    }
    private lateinit var countryCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureOsmdroid()
        binding = ActivityCountryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        citiesMap = CitiesMap(binding.mapView)

        countryCode = intent.getStringExtra(EXTRA_COUNTRY_CODE) ?: run {
            finish()
            return
        }
        binding.toolbar.title = intent.getStringExtra(EXTRA_COUNTRY_NAME)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnReintentarInfo.setOnClickListener { loadCountry() }

        binding.rvCiudades.layoutManager = LinearLayoutManager(this)
        binding.rvCiudades.adapter = cityAdapter
        binding.btnReintentarCiudades.setOnClickListener { loadCities() }

        setupTabs()
        loadCountry()
    }

    private fun setupTabs() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.panelInfo.isVisible = tab.position == 0
                binding.panelCiudades.isVisible = tab.position == 1
                if (tab.position == 1 && !citiesRequested) loadCities()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loadCountry() {
        renderInfo(loading = true)
        lifecycleScope.launch {
            when (val result = controller.getCountryDetail(countryCode)) {
                is ApiResult.Success -> {
                    showCountry(result.data)
                    renderInfo(content = true)
                }
                is ApiResult.Error -> {
                    binding.txtErrorInfo.text = result.error.message
                    renderInfo(error = true)
                }
            }
        }
    }

    private fun renderInfo(loading: Boolean = false, content: Boolean = false, error: Boolean = false) {
        binding.progressInfo.isVisible = loading
        binding.scrollInfo.isVisible = content
        binding.layoutErrorInfo.isVisible = error
    }

    private fun loadCities() {
        citiesRequested = true
        renderCities(loading = true)
        lifecycleScope.launch {
            when (val result = cityController.getTopCities(countryCode)) {
                is ApiResult.Success -> {
                    cityAdapter.submit(result.data.map { CityWeather(it) })
                    citiesMap.showCities(result.data)
                    renderCities(list = true)
                    loadWeatherForCities(result.data)
                }
                is ApiResult.Error -> {
                    binding.txtErrorCiudades.text = result.error.message
                    binding.btnReintentarCiudades.isVisible = result.error.type != ErrorType.NO_CITIES
                    renderCities(error = true)
                }
            }
        }
    }

    private fun loadWeatherForCities(cities: List<GeoCity>) {
        cities.forEach { city ->
            lifecycleScope.launch {
                val state = weatherController.getWeatherFor(city)
                cityAdapter.updateWeather(city.geonameId, state)
                citiesMap.updateWeather(city.geonameId, state)
            }
        }
    }

    private fun renderCities(loading: Boolean = false, list: Boolean = false, error: Boolean = false) {
        binding.progressCiudades.isVisible = loading
        binding.rvCiudades.isVisible = list
        binding.layoutErrorCiudades.isVisible = error
    }

    // ---------- Pintar el país ----------

    private fun showCountry(country: Country) {
        applyFlagTheme(country)

        binding.imgBandera.load(country.flag?.urlPng) { crossfade(true) }
        binding.txtNombre.text = country.displayName
        binding.txtOficial.text = country.displayOfficialName

        val capitals = country.capitals?.mapNotNull { it.name }?.joinToString(", ")
        binding.txtCapital.isVisible = !capitals.isNullOrBlank()
        binding.txtCapital.text = "$capitals"

        binding.statsRow.removeAllViews()
        binding.containerInfo.removeAllViews()

        val numbers = NumberFormat.getInstance()

        addStat("👥", country.population?.let { compact(it.toDouble()) }, "Población")
        addStat("📐", country.area?.kilometers?.let { compact(it) }, "Área km²")
        addStat("💱", country.currencies?.mapNotNull { it.code }?.joinToString("/"), "Moneda")

        val lat = country.coordinates?.lat
        val lng = country.coordinates?.lng
        val coordinates = if (lat != null && lng != null) String.format("%.2f, %.2f", lat, lng) else null

        addSection(
            "📍 Ubicación",
            "Región" to country.region,
            "Subregión" to country.subregion,
            "Continente" to country.continents?.joinToString(", "),
            "Coordenadas" to coordinates,
            "Fronteras" to country.borders?.let {
                if (it.isEmpty()) "Sin fronteras terrestres" else it.joinToString(", ")
            },
            "País sin salida al mar" to country.landlocked?.let { if (it) "Sí" else "No" },
            "Zonas horarias" to country.timezones?.joinToString(", ")
        )

        addSection(
            "👥 Sociedad",
            "Población" to country.population?.let { numbers.format(it) },
            "Idiomas" to country.languages?.joinToString("\n") { language ->
                if (language.nativeName.isNullOrBlank()) language.name.orEmpty()
                else "${language.name} (${language.nativeName})"
            },
            "Monedas" to country.currencies?.joinToString("\n") { currency ->
                val symbol = currency.symbol?.let { ", $it" } ?: ""
                "${currency.name} (${currency.code}$symbol)"
            }
        )

        addSection(
            "Códigos",
            "ISO 3166-1 (alfa-2 / alfa-3)" to
                    listOfNotNull(country.codes?.alpha2, country.codes?.alpha3).joinToString(" / ")
        )
    }

    private fun addStat(emoji: String, value: String?, label: String) {
        val stat = ItemStatBinding.inflate(layoutInflater, binding.statsRow, false)
        stat.txtEmoji.text = emoji
        stat.txtValor.text = if (value.isNullOrBlank()) "—" else value
        stat.txtEtiqueta.text = label
        binding.statsRow.addView(stat.root)
    }

    private fun addSection(title: String, vararg rows: Pair<String, String?>) {
        val validRows = rows.filter { !it.second.isNullOrBlank() }
        if (validRows.isEmpty()) return

        val section = ItemSectionBinding.inflate(layoutInflater, binding.containerInfo, false)
        section.txtTitulo.text = title
        validRows.forEach { (label, value) ->
            val row = ItemInfoRowBinding.inflate(layoutInflater, section.containerFilas, false)
            row.txtEtiqueta.text = label
            row.txtValor.text = value
            section.containerFilas.addView(row.root)
        }
        binding.containerInfo.addView(section.root)
    }

    // ---------- Tema con los colores de la bandera ----------

    private fun applyFlagTheme(country: Country) {
        val color = pickThemeColor(country)
        val onColor = if (ColorUtils.calculateLuminance(color) > 0.5) Color.BLACK else Color.WHITE
        val softOnColor = ColorUtils.setAlphaComponent(onColor, 190)
        val radius = dp(32)

        binding.toolbar.setBackgroundColor(color)
        binding.toolbar.setTitleTextColor(onColor)
        binding.toolbar.setNavigationIconTint(onColor)

        binding.tabs.setBackgroundColor(color)
        binding.tabs.setTabTextColors(softOnColor, onColor)
        binding.tabs.setSelectedTabIndicatorColor(onColor)

        binding.headerPanel.background = GradientDrawable().apply {
            setColor(color)
            cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)
        }
        binding.txtNombre.setTextColor(onColor)
        binding.txtOficial.setTextColor(softOnColor)
        binding.txtCapital.setTextColor(onColor)
        binding.txtCapital.background = GradientDrawable().apply {
            setColor(ColorUtils.setAlphaComponent(onColor, 45))
            cornerRadius = dp(20)
        }
    }

    private fun pickThemeColor(country: Country): Int {
        val colors = country.flag?.colors
        val candidates = listOf(
            colors?.prominent,
            colors?.swatches?.get("vibrant"),
            colors?.swatches?.get("dark_vibrant"),
            colors?.swatches?.get("muted"),
            colors?.dominant
        )
        for (hex in candidates) {
            val parsed = hex?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: continue
            val luminance = ColorUtils.calculateLuminance(parsed)
            if (luminance in 0.03..0.85) return parsed
        }
        return Color.parseColor("#37474F")
    }

    // ---------- Utilidades ----------

    private fun dp(value: Int): Float = value * resources.displayMetrics.density

    private fun compact(n: Double): String = when {
        n >= 1_000_000_000 -> String.format("%.1f mil M", n / 1_000_000_000)
        n >= 1_000_000 -> String.format("%.1f M", n / 1_000_000)
        n >= 1_000 -> String.format("%.1f mil", n / 1_000)
        else -> n.toInt().toString()
    }

    companion object {
        const val EXTRA_COUNTRY_CODE = "country_code"
        const val EXTRA_COUNTRY_NAME = "country_name"
    }

    private fun configureOsmdroid() {
        val config = Configuration.getInstance()
        config.load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        config.userAgentValue = packageName
        config.osmdroidBasePath = File(cacheDir, "osmdroid")
        config.osmdroidTileCache = File(File(cacheDir, "osmdroid"), "tiles")
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.onDetach()
        super.onDestroy()
    }
}