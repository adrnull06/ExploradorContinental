package com.castillo.exploradorcontinental.view

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.castillo.exploradorcontinental.controller.CountryController
import com.castillo.exploradorcontinental.databinding.ActivityCountriesBinding
import com.castillo.exploradorcontinental.model.ApiResult
import kotlinx.coroutines.launch

class CountriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountriesBinding
    private val controller = CountryController()
    private lateinit var regionApiName: String

    private val adapter = CountryAdapter { country ->
        val code = country.codes?.alpha2
        if (code != null) {
            val intent = Intent(this, CountryDetailActivity::class.java).apply {
                putExtra(CountryDetailActivity.EXTRA_COUNTRY_CODE, code)
                putExtra(CountryDetailActivity.EXTRA_COUNTRY_NAME, country.displayName)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regionApiName = intent.getStringExtra(EXTRA_REGION_API) ?: run {
            finish()
            return
        }
        binding.toolbar.title = intent.getStringExtra(EXTRA_REGION_NAME)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.rvPaises.layoutManager = LinearLayoutManager(this)
        binding.rvPaises.adapter = adapter
        binding.btnReintentar.setOnClickListener { loadCountries() }

        loadCountries()
    }

    private fun loadCountries() {
        render(loading = true)
        lifecycleScope.launch {
            when (val result = controller.getCountriesByRegion(regionApiName)) {
                is ApiResult.Success -> {
                    if (result.data.isEmpty()) {
                        render(empty = true)
                    } else {
                        adapter.submit(result.data)
                        render(list = true)
                    }
                }
                is ApiResult.Error -> {
                    binding.txtError.text = result.error.message
                    render(error = true)
                }
            }
        }
    }

    private fun render(
        loading: Boolean = false,
        list: Boolean = false,
        error: Boolean = false,
        empty: Boolean = false
    ) {
        binding.progress.isVisible = loading
        binding.rvPaises.isVisible = list
        binding.layoutError.isVisible = error
        binding.txtVacio.isVisible = empty
    }

    companion object {
        const val EXTRA_REGION_API = "region_api"
        const val EXTRA_REGION_NAME = "region_name"
    }
}