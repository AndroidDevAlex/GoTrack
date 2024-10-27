package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.LocationData
import com.example.data.UserData

@Database(
    version = 2,
    entities = [
        UserData::class, LocationData::class
    ]
)

abstract class MyRoomDB : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    abstract fun getLocationDao(): LocationDao

}