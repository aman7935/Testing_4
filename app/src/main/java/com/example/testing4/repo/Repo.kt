package com.example.testing4.repo

import androidx.lifecycle.LiveData
import com.example.testing4.api.VybeShopApi
import com.example.testing4.database.DbDao
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.models.category.Category
import com.example.testing4.models.entities.OrdersEntity
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response

class Repo(private val vybeShopApi: VybeShopApi, private val dbDao: DbDao) {

    suspend fun getCategories(): Response<Category> {
        return vybeShopApi.getCategories()
    }

    suspend fun getProducts(): Response<Products> {
        return vybeShopApi.getProducts()
    }

    suspend fun getProductByID(id: Int): Response<ProductsItem> {
        return vybeShopApi.getProductById(id)
    }

    suspend fun insertProduct(products: ProductsItem, dataStore: DataStoreManager) {
        val userId = dataStore.getUserId.first()
        dbDao.insert(products.toEntity(userId))
    }

    fun getAllProducts(userId: String): LiveData<List<ProductItemsEntity>> {
        return dbDao.getAllProducts(userId)
    }

    suspend fun deleteProduct(productID: Int, userId: String) {
        dbDao.deleteProduct(productID, userId)
    }

    //cart
    suspend fun saveInCart(productCart: ProductCart) {
        dbDao.insertCart(productCart)
    }

    fun getAllCartItems(userId: String): List<ProductCart> {
        return dbDao.getAllCartItems(userId)
    }

    suspend fun deleteFromCart(pID: Int, userId: String): Boolean {
        return dbDao.deleteCartItem(pID, userId) > 0
    }

    suspend fun incrementQuantity(pID: Int, userId: String) {
        return dbDao.incrementQuantity(pID, userId)
    }

    suspend fun decrementQuantity(pID: Int, userId: String) {
        return dbDao.decrementQuantity(pID, userId)
    }

    //userAddress

    suspend fun saveLocationIfNotExists(location: UserAddress) {
        return dbDao.insertAddress(location)
    }

    suspend fun getAllAddressesByUserId(userId: String): List<UserAddress> {
        return dbDao.getAllAddressesByUserId(userId)
    }

    suspend fun resetDefaultAddress(userId: String) {
        return dbDao.resetDefaultAddress(userId)
    }

    suspend fun getDefaultAddressByUserId(userId: String): UserAddress? {
        return dbDao.getDefaultAddressByUserId(userId)
    }

    suspend fun setDefaultAddressById(userId: String, addressId: Int) {
        return dbDao.setDefaultAddressById(userId, addressId)
    }

    suspend fun makeAddressDefault(id: Int, userId: String) {
        return dbDao.makeAddressDefault(id, userId)
    }

    suspend fun insertOrder(ordersEntity: OrdersEntity) {
        return dbDao.insertOrder(ordersEntity)
    }

    suspend fun saveOrdersAndDeleteFromCart(ordersEntity: OrdersEntity, userId: String) {
        return dbDao.saveOrdersAndDeleteFromCart(ordersEntity, userId)
    }

}