package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.LocationData

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationData): Long

    @Query("DELETE FROM location")
    suspend fun deleteAllCoordinations()

    @Query("SELECT * FROM location")
    suspend fun getAllLocations(): List<LocationData>

    @Delete
    suspend fun deleteLocation(location: LocationData)
}