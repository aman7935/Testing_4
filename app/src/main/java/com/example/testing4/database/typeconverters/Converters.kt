package com.example.testing4.database.typeconverters

import androidx.room.TypeConverter
import com.example.testing4.models.product.ProductsItem
import com.google.common.reflect.TypeToken
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

    @TypeConverter
    fun fromProductsItemList(products: ArrayList<ProductsItem>?): String? {
        return gson.toJson(products)
    }

    @TypeConverter
    fun toProductsItemList(json: String?): ArrayList<ProductsItem>? {
        if (json == null) return null
        val type = object : TypeToken<ArrayList<ProductsItem>>() {}.type
        return gson.fromJson(json, type)
    }


}