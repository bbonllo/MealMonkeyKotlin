package com.bbonllo.mealmonkey.data.marker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MarkerDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMarker(marker: Marker)

    @Query("SELECT * FROM marker_data ORDER BY id ASC")
    fun readAllMarkers(): LiveData<List<Marker>>
}