package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.databinding.HomescreenRecyclerviewClothBinding
import com.example.testing4.models.product.ProductsItem

class ProductAdapter(private val products: List<ProductsItem>, private val onItemClickListenerDetails: OnItemClickListenerDetails) :
    RecyclerView.Adapter<ProductAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ItemViewHolder {
        val binding = HomescreenRecyclerviewClothBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ItemViewHolder, position: Int) {
        holder.bindData(position)
        holder.itemView.setOnClickListener {
            onItemClickListenerDetails.onClickForDetails(products[position].id)
        }

    }

    override fun getItemCount(): Int = products.size

    inner class ItemViewHolder(val binding: HomescreenRecyclerviewClothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            binding.apply {
                title.text = products[position].title
                price.text = products[position].price.toString()
                Glide.with(itemView.context).load(products[position].images[0]).into(imageView2)
            }
        }
    }

}