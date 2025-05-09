package com.example.testing4.clicklisteners

import com.example.testing4.models.product.ProductsItem

interface OnClickDeleteFromCategory {
    fun onClickDeleteFromCategory(item : ProductsItem, userId: String)
}