package com.example.testing4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing4.databinding.OnbordingItemsBinding
import com.example.testing4.models.OnboardingItemModel

class ViewPagerAdapter(val viewPagerItemsModelList: List<OnboardingItemModel>)
    : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {


    class ViewPagerViewHolder(val binding : OnbordingItemsBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewPagerViewHolder {
        val binding = OnbordingItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewPagerViewHolder, position: Int) {
        holder.binding.sliderimage.setImageResource(viewPagerItemsModelList[position].img as Int)
    }

    override fun getItemCount(): Int = viewPagerItemsModelList.size

}