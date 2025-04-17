package com.bbonllo.mealmonkey.data.marker

import com.bbonllo.mealmonkey.data.tag.Tag

data class Marker(
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val phoneNumber: String,
    val webPage: String,
    val article: String,
    val icon: String,
    val isVisited: Boolean,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val tags: List<Tag>
)