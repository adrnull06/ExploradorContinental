package com.castillo.exploradorcontinental.view

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.castillo.exploradorcontinental.databinding.ActivityMainBinding
import com.castillo.exploradorcontinental.model.Regions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvRegiones.layoutManager = LinearLayoutManager(this)
        binding.rvRegiones.adapter = RegionAdapter(Regions.all) { region ->
            binding.rvRegiones.adapter = RegionAdapter(Regions.all) { region ->
                val intent = Intent(this, CountriesActivity::class.java).apply {
                    putExtra(CountriesActivity.EXTRA_REGION_API, region.apiName)
                    putExtra(CountriesActivity.EXTRA_REGION_NAME, region.displayName)
                }
                startActivity(intent)
            }        }
    }
}