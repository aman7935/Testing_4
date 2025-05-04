package com.example.testing4.utils

import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.product.ProductsItem

fun ProductsItem.toEntity() : ProductItemsEntity{
    return ProductItemsEntity(
        id = this.id,
        title = this.title,
        price = this.price,
        image = this.images[0])
}