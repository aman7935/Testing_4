package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testing4.models.product.ProductsItem
import com.stripe.model.Price
import java.util.ArrayList


@Entity(tableName = "OrdersEntity")
data class OrdersEntity(
    @PrimaryKey
    val orderID: String,
    val userId: String,
    val jsonFilePath: String,
    val orderStatus: String,
    val billAmount : Long,
    val deliveryDate : String,
)
