package com.intermediate.storyapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class Remote(
    @PrimaryKey val id: String,
    val previousKey: Int?,
    val nextKey: Int?
)
