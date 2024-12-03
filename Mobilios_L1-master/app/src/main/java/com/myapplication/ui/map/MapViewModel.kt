package com.myapplication.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.myapplication.database.ClosestCoordinate
import com.myapplication.database.MeasurementDao
import com.myapplication.database.SignalDao
import com.myapplication.database.UserDao
import com.myapplication.database.UserDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlin.math.pow

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val matavimaiDao: MeasurementDao = UserDatabase.getDatabase(application).measurementDao()
    private val userDao: UserDao = UserDatabase.getDatabase(application).userDao()

    // LiveData to hold the coordinates from the database
    val coordinatesLiveData: LiveData<List<Pair<Int, Int>>> = liveData {
        val coordinates = matavimaiDao.getAllCoordinates().map { it.x_coordinate to it.y_coordinate }
        emit(coordinates)
    }


    suspend fun findClosestCoordinatesForUser(macAddress: String): List<ClosestCoordinate> {
        // Retrieve all user entries associated with the given MAC address
        val userEntries = userDao.getUsersByMacAddress(macAddress)

        // Create a list to store closest coordinates for each user
        val closestCoordinates = mutableListOf<ClosestCoordinate>()

        // For each user entry, find the closest coordinate
        userEntries.forEach { user ->
            // Fetch the coordinates with intensities
            val coordinatesWithIntensities = matavimaiDao.getCoordinatesWithIntensities()

            // Calculate distances and find the closest coordinate
            val closestCoordinate = coordinatesWithIntensities
                .map { coordinate ->
                    // Calculate the distance using the user's signal strengths
                    val distance = calculateEuclideanDistance(
                        user.signalStrength1,
                        user.signalStrength2,
                        user.signalStrength3,
                        coordinate.intensity1,
                        coordinate.intensity2,
                        coordinate.intensity3
                    )
                    Pair(coordinate, distance) // Pair the coordinate with its calculated distance
                }
                .minByOrNull { it.second } // Find the minimum distance

            // Add the closest coordinate to the list if found
            closestCoordinate?.let {
                closestCoordinates.add(ClosestCoordinate(
                    it.first.xCoordinate,
                    it.first.yCoordinate,
                    it.second // Distance
                ))
            }
        }

        return closestCoordinates
    }

    fun getAllMacAddresses(): Flow<List<String>> {
        return userDao.getAllUsers().transform { users ->
            emit(users.map { it.macAddress }.distinct()) // Emit a transformed list of MAC addresses
        }
    }

    // Function to calculate Euclidean distance
    private fun calculateEuclideanDistance(
        userSignal1: Int,
        userSignal2: Int,
        userSignal3: Int,
        intensity1: Int,
        intensity2: Int,
        intensity3: Int
    ): Double {
        return Math.sqrt(
            (Math.abs(userSignal1 - intensity1).toDouble().pow(2)) +
                    (Math.abs(userSignal2 - intensity2).toDouble().pow(2)) +
                    (Math.abs(userSignal3 - intensity3).toDouble().pow(2))
        )
    }
}
