package com.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class Signal(
    @PrimaryKey val id: Int,
    val intensity: Int,
    val reading: Int,
    val sensor: String
)
