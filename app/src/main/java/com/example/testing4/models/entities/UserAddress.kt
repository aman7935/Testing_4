package com.example.testing4.models.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "UserAddress", indices = [Index(value = ["userId", "apartmentOrHouseNo", "pinCode"], unique = true)])
data class UserAddress(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    val address: String,
    val pinCode : Int,
    val addressType: String,
    val apartmentOrHouseNo : String,
    val streetDetails : String,
    val landmark : String
)
