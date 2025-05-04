package com.example.testing4.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testing4.models.entities.ProductItemsEntity


@Dao
interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dbTable: ProductItemsEntity)

    @Query("SELECT * FROM ProductItemsEntity")
    fun getAllProducts(): LiveData<List<ProductItemsEntity>>

        /*used live data because it automatically updates
        products from db when there is any change in database*/

    @Query("DELETE FROM ProductItemsEntity WHERE id = :productID")
    suspend fun deleteProduct(productID: Int)
}