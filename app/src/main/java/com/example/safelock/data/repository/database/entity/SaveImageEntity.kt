package com.example.safelock.data.repository.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_image")
data class SaveImageEntity(
    @PrimaryKey
    @ColumnInfo(name = "imageUrl")
    val imageUrl: String,

    @ColumnInfo(name = "imageTitle")
    val imageTitle: String
)
