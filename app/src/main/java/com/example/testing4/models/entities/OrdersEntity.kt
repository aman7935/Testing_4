package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testing4.models.product.ProductsItem


@Entity(tableName = "OrdersEntity")
data class OrdersEntity(
    @PrimaryKey
    val pID: Int ,
    val userId: String,
    val quantity: Int,
    val products: ProductsItem,
    val orderStatus: String,
    val billAmount : Long,
    val orderID: String,
    val deliveryDate : String
)
