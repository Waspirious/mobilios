package com.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(measurements: List<Measurement>)

    @Query("SELECT * FROM measurements")
    fun getAllMeasurements(): Flow<List<Measurement>>

    @Query("DELETE FROM measurements") // Clear all measurements
    suspend fun clearOldMeasurements()

    @Query("DELETE FROM sqlite_sequence WHERE name='measurements'") // Reset ID counter
    suspend fun resetIdCounter()

    @Query("SELECT x_coordinate, y_coordinate FROM measurements")
    suspend fun getAllCoordinates(): List<MeasurmentCoordinates>

    @Query("""
    SELECT m.x_coordinate AS xCoordinate, 
           m.y_coordinate AS yCoordinate,
           MAX(CASE WHEN s.sensor = 'wiliboxas1' THEN s.intensity END) AS intensity1,
           MAX(CASE WHEN s.sensor = 'wiliboxas2' THEN s.intensity END) AS intensity2,
           MAX(CASE WHEN s.sensor = 'wiliboxas3' THEN s.intensity END) AS intensity3
    FROM measurements m
    INNER JOIN signals s ON m.reading = s.reading
    GROUP BY m.x_coordinate, m.y_coordinate
    HAVING COUNT(DISTINCT s.sensor) = 3
""")
    suspend fun getCoordinatesWithIntensities(): List<CoordinateWithIntensities>
}

data class MeasurmentCoordinates(
    @ColumnInfo(name = "x_coordinate") val x_coordinate: Int,
    @ColumnInfo(name = "y_coordinate") val y_coordinate: Int
)