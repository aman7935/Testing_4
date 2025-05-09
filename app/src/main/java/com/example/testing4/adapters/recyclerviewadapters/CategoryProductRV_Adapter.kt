package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.R
import com.example.testing4.clicklisteners.OnClickDeleteFromCategory
import com.example.testing4.clicklisteners.OnClickSave
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.clicklisteners.OnItemClickSaveInProductCart
import com.example.testing4.databinding.CategoryProductIvBinding
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.utils.ViewUtils
import com.example.testing4.views.auth.userId

class CategoryProductRV_Adapter(
    private var itemList: List<ProductsItem>,
    private val onItemClickListenerForDetails: OnItemClickListenerDetails,
    private val onClickSave: OnClickSave,
    private val onClickDeleteFromCategory: OnClickDeleteFromCategory,
    private val onItemClickSaveInProductCart: OnItemClickSaveInProductCart
) : RecyclerView.Adapter<CategoryProductRV_Adapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: CategoryProductIvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: ProductsItem) {
            binding.apply {
                Glide.with(itemView.context).load(item.images[0]).into(imageView2)
                title.text = item.title
                price.text = "₹ ${item.price}"

                imageView2.setOnClickListener { onItemClickListenerForDetails.onClickForDetails(item.id) }

                // favorites
                if (item.isFavourite == 1) ViewUtils.setIconColor(binding.likeButton, R.color.like_color, itemView.context)
                else ViewUtils.setIconColor(binding.likeButton, R.color.default_icon_color, itemView.context)

                if (item.inCart == 1){      // cart
                    addToBagTv.text = "Added Already"
                    addToBag.isEnabled = false
                }
                else addToBagTv.text = "Add to Bag"

                likeButton.setOnClickListener {
                    if (item.isFavourite == 1){
                        item.isFavourite = 0
                        onClickDeleteFromCategory.onClickDeleteFromCategory(item, userId)
                        Toast.makeText(itemView.context, "Deleted from Favorites", Toast.LENGTH_SHORT)
                            .show()
                        ViewUtils.setIconColor(binding.likeButton, R.color.default_icon_color, itemView.context)
                    }
                    else if (item.isFavourite == 0){
                        item.isFavourite = 1
                        ViewUtils.setIconColor(binding.likeButton, R.color.like_color, itemView.context)
                        onClickSave.onSaveProduct(item)
                        Toast.makeText(itemView.context, "Saved to Favorites", Toast.LENGTH_SHORT).show()
                    }
                }
                addToBag.setOnClickListener {
                    if (item.inCart == 0) {
                        item.inCart = 1
                        addToBagTv.text = "Added Already"
                        onItemClickSaveInProductCart.onClickSaveInCart(userId , item)
                        addToBag.isEnabled = false
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