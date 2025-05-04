package com.example.testing4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testing4.models.entities.ProductItemsEntity


@Database(entities = [ProductItemsEntity::class], version = 1)
abstract class Database : RoomDatabase(){

    abstract val  dbDao : DbDao
}