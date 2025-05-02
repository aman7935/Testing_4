package com.example.testing4.adapters.viewpageradapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing4.databinding.HomescreenViewpagerItemBinding
import com.example.testing4.models.viewpagermodels.HomeScreenViewPagerModel

class HomeScreenViewPagerAdapter(private val viewPageritems : List<HomeScreenViewPagerModel>) : RecyclerView.Adapter<HomeScreenViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = HomescreenViewpagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = viewPageritems.size

    inner class  ViewPagerViewHolder(val binding : HomescreenViewpagerItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.imageView.setImageResource(viewPageritems[position].img)
        }
    }
}