package com.example.testing4.models.product

data class ProductsItem(
    val category: Category,
    val creationAt: String,
    val description: String,
    val id: Int,
    val images: List<String>,
    val price: Int,
    val slug: String,
    val title: String,
    val updatedAt: String,
    var isFavourite : Int = 0
)