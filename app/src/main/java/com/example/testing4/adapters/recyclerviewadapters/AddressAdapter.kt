package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing4.databinding.AddressItemViewBinding
import com.example.testing4.models.entities.UserAddress

class AddressAdapter(private val addressList: List<UserAddress>) : RecyclerView.Adapter<AddressAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(val binding : AddressItemViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(position: Int){
            val address = addressList[position]
            binding.addressText.text = address.address
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.ItemViewHolder {
        val binding = AddressItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressAdapter.ItemViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int = addressList.size
}