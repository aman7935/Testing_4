package com.example.testing4.database

import android.content.Context
import androidx.room.Room


object DataBaseProvider {

    @Volatile
    private var INSTANCE : Database? = null

    fun getInstance(context: Context) : Database{
        return INSTANCE ?: synchronized(this) {
            var instance = Room.databaseBuilder(context.applicationContext, Database::class.java, "DataBase").build()
            INSTANCE = instance
            instance
        }
    }
}