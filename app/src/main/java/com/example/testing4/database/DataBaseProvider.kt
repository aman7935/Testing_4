package com.example.testing4.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


object DataBaseProvider {

    @Volatile
    private var INSTANCE: Database? = null

    fun getInstance(context: Context): Database {
        return INSTANCE ?: synchronized(this) {
            var instance =
                Room.databaseBuilder(context.applicationContext, Database::class.java, "DataBase")
                    .fallbackToDestructiveMigration(true).build()
            INSTANCE = instance
            instance
        }
    }

    /*val migration = object : Migration(1, 2){      // adding userId column to ProductItemsEntity table
        override fun migrate(db: SupportSQLiteDatabase) {
            super.migrate(db)
            db.execSQL("ALTER TABLE ProductItemsEntity ADD COLUMN userId INTEGER DEFAULT 0 NOT NULL")
        }
    }
    val migration2_3 = object : Migration(2, 3){
        override fun migrate(db: SupportSQLiteDatabase) {
            super.migrate(db)
            db.execSQL("ALTER TABLE ProductCart ADD COLUMN id INTEGER DEFAULT 0 NOT NULL")
        }
    }*/

    /*val migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            super.migrate(db)
            db.execSQL("ALTER TABLE ProductItemsEntity ADD COLUMN isFavorite BOOLEAN TRUE DEFAULT 0")
        }
    }
    val migration2 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            super.migrate(db)
            db.execSQL("ALTER TABLE ProductItemsEntity ADD COLUMN description TEXT")
        }
    }*/
}