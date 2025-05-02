package com.example.testing4.api

import com.example.testing4.models.category.Category
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VybeShopApi {

    @GET("categories")
    suspend fun getCategories(): Response<Category>

    @GET("products")
    suspend fun getProducts() : Response<Products>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId : Int) : Response<ProductsItem>

}