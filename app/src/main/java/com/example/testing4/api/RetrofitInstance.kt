package com.example.testing4.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.escuelajs.co/api/v1/"

    val retroFitApi : VybeShopApi by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VybeShopApi::class.java)
    }
}