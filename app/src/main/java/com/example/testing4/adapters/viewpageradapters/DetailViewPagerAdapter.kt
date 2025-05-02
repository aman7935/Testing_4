package com.example.testing4.adapters.viewpageradapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.R
import com.example.testing4.databinding.DetailsVpIvBinding

class DetailViewPagerAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<DetailViewPagerAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: DetailsVpIvBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailViewPagerAdapter.ItemViewHolder {
        val binding = DetailsVpIvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DetailViewPagerAdapter.ItemViewHolder,
        position: Int
    ) {
        Glide.with(holder.itemView.context)
            .load(imageList[position])
            .placeholder(R.drawable.banner_5).into(holder.binding.imgID)
    }

    override fun getItemCount(): Int = imageList.size
}