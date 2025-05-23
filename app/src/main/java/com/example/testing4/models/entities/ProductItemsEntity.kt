package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ProductItemsEntity")
data class ProductItemsEntity (
    @PrimaryKey
    val id : Int = 0,
    val title : String,
    val price : Int,
    val image : String,
    val userId : String
)