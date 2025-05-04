package com.example.testing4.repo

import androidx.lifecycle.LiveData
import com.example.testing4.api.VybeShopApi
import com.example.testing4.database.DbDao
import com.example.testing4.models.category.Category
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.utils.toEntity
import retrofit2.Response

class Repo(private val vybeShopApi: VybeShopApi, private val DbDao: DbDao) {

    suspend fun getCategories() : Response<Category> {
        return vybeShopApi.getCategories()
    }
    suspend fun getProducts() : Response<Products> {
        return vybeShopApi.getProducts()
    }
    suspend fun getProductByID(id : Int) : Response<ProductsItem> {
        return vybeShopApi.getProductById(id)
    }

    suspend fun insertProduct(products : ProductsItem){
        DbDao.insert(products.toEntity())
    }

    fun getAllProducts() : LiveData<List<ProductItemsEntity>>{
        return DbDao.getAllProducts()
    }

    suspend fun deleteProduct(productID: Int){
        DbDao.deleteProduct(productID)
    }
}