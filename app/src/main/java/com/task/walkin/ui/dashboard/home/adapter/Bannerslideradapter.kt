package com.task.walkin.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.walkin.databinding.ItemBannerSliderBinding
import com.task.walkin.model.BannerItem

class BannerSliderAdapter(private val items: List<BannerItem>) :
    RecyclerView.Adapter<BannerSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val binding: ItemBannerSliderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemBannerSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView)
            .load(item.imageResId)
            .placeholder(com.task.walkin.R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.binding.sliderImageView)
    }

    override fun getItemCount() = items.size
}
