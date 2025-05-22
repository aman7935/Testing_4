package com.example.testing4.adapters.recyclerviewadapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.R
import com.example.testing4.databinding.OrdersItemViewBinding
import com.example.testing4.models.entities.OrdersEntity

class OrderAdapter(private val orderList: List<OrdersEntity>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            OrdersItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int = orderList.size

    inner class ViewHolder(val binding: OrdersItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}