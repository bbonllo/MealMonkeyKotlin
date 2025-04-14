package com.bbonllo.mealmonkey.data.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_data")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String
)
