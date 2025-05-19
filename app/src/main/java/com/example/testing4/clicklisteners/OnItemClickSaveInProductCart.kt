package com.example.testing4.clicklisteners

import com.example.testing4.models.product.ProductsItem

interface OnItemClickSaveInProductCart {
    fun onClickSaveInCart(  productsItem: ProductsItem)
}