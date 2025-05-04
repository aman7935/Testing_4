package com.example.testing4.adapters.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testing4.clicklisteners.OnItemClickDelete
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.databinding.FavoritesRvIvBinding
import com.example.testing4.models.entities.ProductItemsEntity


class FavouritesAdapter(
    private var favouritesList: List<ProductItemsEntity>,
    private val onItemClickListenerDetails: OnItemClickListenerDetails,
    private val onItemClickDelete: OnItemClickDelete
) : RecyclerView.Adapter<FavouritesAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: FavoritesRvIvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            val item = favouritesList[position]

            binding.apply {
                nameTv.text = item.title
                priceTv.text = item.price.toString()
                Glide.with(imgID.context).load(item.image).into(imgID)

                imgID.setOnClickListener {
                    onItemClickListenerDetails.onClickForDetails(item.id)
                }
                itemView.setOnClickListener {
                    onItemClickListenerDetails.onClickForDetails(item.id)
                }
                dislikeBtn.setOnClickListener {
                    onItemClickDelete.onClickDelete(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = FavoritesRvIvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int = favouritesList.size


    fun updateList(product : List<ProductItemsEntity>){
        favouritesList = product
        notifyDataSetChanged()
    }
}
