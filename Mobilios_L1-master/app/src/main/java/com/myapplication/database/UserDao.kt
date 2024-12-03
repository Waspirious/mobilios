package com.myapplication.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM user_table WHERE macAddress = :macAddress")
    suspend fun getUsersByMacAddress(macAddress: String): List<User>

    @Update
    suspend fun update(user: User)
}