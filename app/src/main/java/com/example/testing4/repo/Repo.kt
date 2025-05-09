package com.example.testing4.repo

import androidx.lifecycle.LiveData
import com.example.testing4.api.VybeShopApi
import com.example.testing4.database.DbDao
import com.example.testing4.models.category.Category
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.utils.toEntity
import retrofit2.Response

class Repo(private val vybeShopApi: VybeShopApi, private val dbDao: DbDao) {

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
        dbDao.insert(products.toEntity())
    }
    fun getAllProducts(userId: String) : LiveData<List<ProductItemsEntity>>{
        return dbDao.getAllProducts(userId)
    }
    suspend fun deleteProduct(productID: Int, userId: String){
        dbDao.deleteProduct(productID, userId)
    }

    //cart
    suspend fun saveInCart( productCart: ProductCart){
        dbDao.insertCart( productCart)
    }
    fun getAllCartItems(userId: String) : List<ProductCart>{
        return dbDao.getAllCartItems(userId)
    }
    suspend fun deleteFromCart(pID : Int, userId: String){
        return dbDao.deleteCartItem(pID, userId)
    }
    suspend fun incrementQuantity(pID: Int, userId: String){
        return dbDao.incrementQuantity(pID,  userId)
    }
    suspend fun decrementQuantity(pID: Int, userId: String){
        return dbDao.decrementQuantity(pID,  userId)
    }


}