package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testing4.clicklisteners.OnClickListenerForAddress
import com.example.testing4.databinding.AddressItemViewBinding
import com.example.testing4.models.entities.UserAddress

class AddressAdapter(
    private var addressList: List<UserAddress>,
    private val onClickListenerForAddress: OnClickListenerForAddress
) : RecyclerView.Adapter<AddressAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: AddressItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val address = addressList[position]
            binding.titleText.text = address.addressType
            binding.addressText.text = "${address.apartmentOrHouseNo}, ${address.streetDetails}"
            itemView.setOnClickListener {
                onClickListenerForAddress.onClickForAddress(address)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            AddressItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int = addressList.size

    // Add this function to update the list and refresh the RecyclerView
    fun updateList(newList: List<UserAddress>) {
        addressList = newList
        notifyDataSetChanged()
    }
}
