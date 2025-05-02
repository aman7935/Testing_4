package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.clicklisteners.OnItemClickListener
import com.example.testing4.databinding.CategoryItemViewBinding
import com.example.testing4.models.category.CategoryItem

class CategoryRVAdapter(private val categoryItemList: List<CategoryItem>, private val onItemClickListener : OnItemClickListener) : RecyclerView.Adapter<CategoryRVAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {
            titleID.text = categoryItemList[position].name
            Glide.with(holder.itemView.context).load(categoryItemList[position].image).into(imgID)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onclick(categoryItemList[position].name)
        }
    }

    override fun getItemCount(): Int = categoryItemList.size

    inner class CategoryViewHolder(val binding : CategoryItemViewBinding) : RecyclerView.ViewHolder(binding.root) {}
}