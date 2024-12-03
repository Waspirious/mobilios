package com.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reading: Int,
    val x_coordinate: Int,
    val y_coordinate: Int,
    val distance: Double
)