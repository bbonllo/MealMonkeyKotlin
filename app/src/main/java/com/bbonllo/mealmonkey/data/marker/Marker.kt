package com.bbonllo.mealmonkey.data.marker

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bbonllo.mealmonkey.data.tag.Tag

@Entity(tableName = "marker_data")
data class Marker(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val icon: String,
    val isVisited: Boolean,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val tags: List<Tag>
)