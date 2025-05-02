package com.example.testing4.models.category

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CategoryItem(
    val creationAt: String,
    val id: Int,
    val image: String,
    val name: String,
    val slug: String,
    val updatedAt: String
)

