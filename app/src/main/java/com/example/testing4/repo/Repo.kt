package com.example.testing4.repo

import com.example.testing4.api.VybeShopApi
import com.example.testing4.models.category.Category
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import retrofit2.Response

class Repo(private val vybeShopApi: VybeShopApi) {
    suspend fun getCategories() : Response<Category> {
        return vybeShopApi.getCategories()
    }
    suspend fun getProducts() : Response<Products> {
        return vybeShopApi.getProducts()
    }
    suspend fun getProductByID(id : Int) : Response<ProductsItem> {
        return vybeShopApi.getProductById(id)
    }
}