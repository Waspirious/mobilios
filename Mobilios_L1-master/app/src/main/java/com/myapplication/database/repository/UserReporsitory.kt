package com.myapplication.database.repository
import com.myapplication.database.Signal
import com.myapplication.database.SignalDao
import com.myapplication.database.User
import com.myapplication.database.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun deleteAll(): Int{
        return userDao.deleteAll()
    }

    suspend fun findUsersByMacAddress(macAddress: String): List<User> {
        return userDao.getUsersByMacAddress(macAddress)
    }

    suspend fun update(user: User) {
        userDao.update(user) // Call the DAO's update method
    }
}