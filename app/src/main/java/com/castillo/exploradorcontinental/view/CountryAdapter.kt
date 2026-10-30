package com.castillo.exploradorcontinental.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.castillo.exploradorcontinental.databinding.ItemCountryBinding
import com.castillo.exploradorcontinental.model.Country

class CountryAdapter(
    private val onClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    private var countries: List<Country> = emptyList()

    class ViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root)

    fun submit(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.binding.txtPais.text = country.displayName
        holder.binding.txtCapital.text =
            country.primaryCapital?.name?.let { "Capital: $it" } ?: "Sin capital registrada"
        holder.binding.imgBandera.load(country.flag?.urlPng) {
            crossfade(true)
        }
        holder.binding.root.setOnClickListener { onClick(country) }
    }

    override fun getItemCount() = countries.size
}