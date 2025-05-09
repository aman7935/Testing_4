package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testing4.models.product.ProductsItem


@Entity(tableName = "ProductCart")
data class ProductCart(

    @PrimaryKey
    val pID: Int ,
    val userId: String,
    val quantity: Int,
    val products: ProductsItem,
)
