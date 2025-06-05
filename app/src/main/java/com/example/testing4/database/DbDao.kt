package com.example.testing4.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.testing4.models.entities.OrdersEntity
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.entities.UserAddress


@Dao
interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbTable: ProductItemsEntity)

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbTable: ProductItemsEntity, userId: String)*/


    @Query("SELECT * FROM ProductItemsEntity WHERE userId = :userId")
    fun getAllProducts(userId: String): LiveData<List<ProductItemsEntity>>

    @Query("DELETE FROM ProductItemsEntity WHERE id = :productID AND userId = :userId")
    suspend fun deleteProduct(productID: Int, userId: String)

    //cursor
    @Query("SELECT * FROM ProductItemsEntity WHERE userId = :userId")
    fun getAllProductsByUserId(userId: String): Cursor


    // table for cart products
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(productCart: ProductCart)

    @Query("SELECT * FROM ProductCart WHERE userId = :userId")
    fun getAllCartItems(userId: String): List<ProductCart>

    @Query("DELETE FROM ProductCart WHERE pID = :productID AND userId = :userId")
    suspend fun deleteCartItem(productID: Int, userId: String) : Int

    @Query("UPDATE ProductCart SET quantity = quantity + 1 WHERE pID = :productID AND userId = :userId")
    suspend fun incrementQuantity(productID: Int, userId: String)

    @Query("UPDATE ProductCart SET quantity = quantity - 1 WHERE pID = :productID AND userId = :userId")
    suspend fun decrementQuantity(productID: Int, userId: String)


    // table for user address
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddress(userAddress: UserAddress)

    /*@Query("SELECT * FROM UserAddress WHERE userId = :userId AND address = :address")
    suspend fun getAddressByUserIdAndAddress(userId: String, address: String): UserAddress?*/

    /*@Query("SELECT COUNT(*) FROM UserAddress WHERE userId = :userId AND latitude = :latitude AND longitude = :longitude")
    suspend fun countAddressesByUserIdAndLocation(userId: String, latitude: Double, longitude: Double): Int*/


    @Query("SELECT * FROM UserAddress WHERE userId = :userId")
    suspend fun getAllAddressesByUserId(userId: String): List<UserAddress>

    @Query("UPDATE UserAddress SET defaultAddress = 0 WHERE userId = :userId")
    suspend fun resetDefaultAddress(userId: String)

    @Query("SELECT * FROM UserAddress WHERE userId = :userId AND defaultAddress = 1")
    suspend fun getDefaultAddressByUserId(userId: String): UserAddress?

    @Query("UPDATE UserAddress SET defaultAddress = 1 WHERE userId = :userId AND id = :addressId")
    suspend fun setDefaultAddressById(userId: String, addressId: Int)

    @Transaction
    suspend fun makeAddressDefault(id:Int,userId:String)
    {
        resetDefaultAddress(userId)
        setDefaultAddressById(userId,id)
    }


    // table for orders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(ordersEntity: OrdersEntity)


    @Query("SELECT * FROM OrdersEntity WHERE userId = :userId")
    fun getAllOrders(userId: String): LiveData<List<OrdersEntity>>

    @Query("DELETE FROM OrdersEntity WHERE orderID = :orderID AND userId = :userId")
    suspend fun deleteOrder(orderID: String, userId: String)


    @Transaction
    suspend fun saveOrdersAndDeleteFromCart(ordersEntity: OrdersEntity, userId: String) {
        insertOrder(ordersEntity)
    }

}