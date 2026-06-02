package com.task.walkin.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.walkin.databinding.ItemCategoryBinding

import com.task.walkin.model.Category

class CategoryAdapter(
    private val items: List<Category>,
    private val onItemClick: ((Category) -> Unit)? = null
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            categoryImg.setImageResource(item.imageResId)
            tileTitle.text = item.title
            root.setOnClickListener { onItemClick?.invoke(item) }
        }
    }

    override fun getItemCount(): Int = items.size
}