package com.example.testing4.adapters.recyclerviewadapters

import android.util.Log
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
            if (addressList[position].defaultAddress == 1) {
                binding.radioButton.isChecked = true
            } else {
                binding.radioButton.isChecked = false
            }

            binding.titleText.text = address.addressType
            binding.addressText.text = "${address.apartmentOrHouseNo}, ${address.streetDetails}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            AddressItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(position)


        holder.binding.radioButton.setOnClickListener {
            onClickListenerForAddress.onClickForAddress(addressList[position])
            /* if (address.defaultAddress == 1){
                 binding.radioButton.isChecked = true

             }else{
                 binding.radioButton.isChecked = false
             }*/

        }
    }

    override fun getItemCount(): Int = addressList.size

    // Add this function to update the list and refresh the RecyclerView
    fun updateList(newList: List<UserAddress>) {
        addressList = newList
        Log.d("TAG", "updateList: $addressList")
        Log.d("TAG", "updateList: $newList")
        notifyDataSetChanged()
    }
}
