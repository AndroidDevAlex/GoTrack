package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double,

    val longitude: Double,

    val timeGettingCoordinate: Long,

    val timeToServer: Long

) {

    constructor() : this(0, 0.0, 0.0, 0L, 0L)
}
