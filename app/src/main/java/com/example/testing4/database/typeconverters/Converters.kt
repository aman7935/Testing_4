package com.example.testing4.database.typeconverters

import androidx.room.TypeConverter
import com.example.testing4.models.product.ProductsItem
import com.google.gson.Gson

class Converters {
    val gson = Gson()

    @TypeConverter
    fun fromProductsItems(products: ProductsItem): String? {
        return gson.toJson(products)
    }

    @TypeConverter
    fun toProductsItems(json: String?): ProductsItem? {
        return gson.fromJson(json, ProductsItem::class.java)
    }

}