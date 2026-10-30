package com.castillo.exploradorcontinental.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.castillo.exploradorcontinental.databinding.ItemRegionBinding
import com.castillo.exploradorcontinental.model.Region

// Clase que conecta los datos con la vista
class RegionAdapter(
    private val regions: List<Region>,
    private val onClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRegionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val region = regions[position]
        holder.binding.txtEmoji.text = region.emoji
        holder.binding.txtRegion.text = region.displayName
        holder.binding.root.setOnClickListener { onClick(region) }
    }

    override fun getItemCount() = regions.size
}