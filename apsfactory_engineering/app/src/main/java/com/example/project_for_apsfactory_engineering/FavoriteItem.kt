package com.example.project_for_apsfactory_engineering

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val id: Int,
    val title: String,
    val artist: String,
    val imageUrl: String?
)
