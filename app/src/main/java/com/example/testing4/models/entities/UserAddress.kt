package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserAddress")
data class UserAddress(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val UserId: String,
    val address: String,
)
