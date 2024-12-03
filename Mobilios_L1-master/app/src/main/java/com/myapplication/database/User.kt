package com.myapplication.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "macAddress") val macAddress: String,
    @ColumnInfo(name = "signalStrength1") val signalStrength1: Int,
    @ColumnInfo(name = "signalStrength2") val signalStrength2: Int,
    @ColumnInfo(name = "signalStrength3") val signalStrength3: Int
)