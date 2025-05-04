package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.R
import com.example.testing4.clicklisteners.OnClickSave
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.databinding.CategoryProductIvBinding
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.utils.ViewUtils

class CategoryProductRV_Adapter(
    private var itemList: List<ProductsItem>,
    private val onItemClickListenerForDetails: OnItemClickListenerDetails,
    private val onClickSave: OnClickSave
) : RecyclerView.Adapter<CategoryProductRV_Adapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: CategoryProductIvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var isFavorites = false

        fun bindData(item: ProductsItem) {
            binding.apply {
                Glide.with(itemView.context).load(item.images[0]).into(imageView2)
                title.text = item.title
                price.text = "₹ ${item.price}"

                imageView2.setOnClickListener { onItemClickListenerForDetails.onClickForDetails(item.id) }

                likeButton.setOnClickListener {
                    isFavorites = !isFavorites
                    val newColor =
                        if (isFavorites) R.color.like_color else R.color.default_icon_color
                    ViewUtils.setIconColor(binding.likeButton, newColor, itemView.context)

                    if (isFavorites){
                        onClickSave.onSaveProduct(item)
                        Toast.makeText(itemView.context, "Saved to Favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryProductRV_Adapter.ItemViewHolder {
        val binding =
            CategoryProductIvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryProductRV_Adapter.ItemViewHolder,
        position: Int
    ) {
        holder.bindData(itemList[position])

    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(list: List<ProductsItem>) {
        this.itemList = list
        notifyDataSetChanged()
    }

}