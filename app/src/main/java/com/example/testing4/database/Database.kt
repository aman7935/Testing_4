package com.example.testing4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL
import com.example.testing4.database.typeconverters.Converters
import com.example.testing4.models.entities.OrdersEntity
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.entities.UserAddress

@Database(entities = [ProductItemsEntity::class, ProductCart::class, UserAddress::class, OrdersEntity::class], version = 24)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase(){
    abstract val  dbDao : DbDao
}
