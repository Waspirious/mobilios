package com.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signals: List<Signal>)

    @Query("SELECT * FROM signals")
    fun getAllSignalStrengths(): Flow<List<Signal>>

    @Query("DELETE FROM signals") // Clear all measurements
    suspend fun clearOldMeasurements()

    @Query("DELETE FROM sqlite_sequence WHERE name='signals'") // Reset ID counter
    suspend fun resetIdCounter()

}