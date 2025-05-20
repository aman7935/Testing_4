package com.example.testing4.adapters.recyclerviewadapters

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.R
import com.example.testing4.clicklisteners.OnClickDecrement
import com.example.testing4.clicklisteners.OnClickIncrement
import com.example.testing4.clicklisteners.OnItemClickDelete
import com.example.testing4.clicklisteners.OnItemClickDeleteCart
import com.example.testing4.databinding.CartItemIvBinding
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.product.ProductsItem
import kotlin.random.Random

class CartRvAdapter(
    private var cartItemList: ArrayList<ProductsItem>,
    private val onItemClickDeleteCart: OnItemClickDeleteCart,
    private val onClickIncrement: OnClickIncrement,
    private val onClickDecrement: OnClickDecrement,
    private val calculateBillDetails : () -> Unit
) : RecyclerView.Adapter<CartRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemIvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(cartItemList[position])
    }

    override fun getItemCount(): Int = cartItemList.size

    fun updateList(newList: ArrayList<ProductsItem>) {
        cartItemList = newList
        notifyDataSetChanged()
        calculateBillDetails()
    }

    inner class ViewHolder(val binding: CartItemIvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: ProductsItem) {
            val itemPrice = item.price * 85
            val mrpPrice = Random.nextInt(itemPrice+1, itemPrice + 200)
            val rating = String.format("%.1f", (3.0 + Random.nextFloat() * (5.0 - 3.0)))
            Log.d("TAG", "rating: $rating")
            binding.apply {
                nameTv.text = item.title
                priceTv.text = "₹ $itemPrice"
                desc.text = item.description
                ratingTv.text = rating
                MRP.text = "₹ $mrpPrice"
                MRP.paintFlags = MRP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


                number.text = item.quantity.toString()

                Glide.with(itemView.context)
                    .load(item.images.firstOrNull())
                    .placeholder(R.drawable.landscape_placeholder_svgrepo_com)
                    .into(imgID)


                deleteBTN.setOnClickListener {
                    onItemClickDeleteCart.onclickDelete(item)
                    calculateBillDetails()
                }

                increment.setOnClickListener {
                    if (item.quantity >= 1){
                        onClickIncrement.onClickIncrement(item)
                        item.quantity += 1
                        number.text=item.quantity.toString()
                        decrement.isEnabled = true
                        calculateBillDetails()
                    }
                }
                decrement.setOnClickListener {
                    if (item.quantity > 1) {
                        decrement.isEnabled = true
                        onClickDecrement.onClickDecrement(item)
                        item.quantity -= 1
                        number.text=item.quantity.toString()
                        calculateBillDetails()
                    }
                    else if(item.quantity == 1){
                        decrement.isEnabled = false
                    }
                }
            }
        }
    }
}

