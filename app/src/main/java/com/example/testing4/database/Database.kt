package com.example.testing4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL
import com.example.testing4.models.entities.ProductItemsEntity


@Database(entities = [ProductItemsEntity::class], version = 5)
abstract class Database : RoomDatabase(){
    abstract val  dbDao : DbDao
}
