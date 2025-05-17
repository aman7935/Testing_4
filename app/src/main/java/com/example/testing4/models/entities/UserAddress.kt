package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserAddress")
data class UserAddress(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
)
