package com.bbonllo.mealmonkey.data.tag

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagConverter {
    @TypeConverter
    fun fromString(value: String): List<Tag> {
        val listType = object : TypeToken<List<Tag>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Tag>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
