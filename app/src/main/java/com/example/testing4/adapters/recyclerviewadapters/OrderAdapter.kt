package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing4.databinding.ItemViewOrderlistBinding
import com.example.testing4.models.entities.OrdersEntity

class OrderAdapter(private val orderList: List<OrdersEntity>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemViewOrderlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int = orderList.size

    inner class ViewHolder(val binding: ItemViewOrderlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            val order = orderList[position]
            binding.orderId.text = "Order: #${order.orderID}"
            binding.deliveryDate.text = order.deliveryDate
            binding.totalAmount.text = "$${order.billAmount}"
        }
    }
}