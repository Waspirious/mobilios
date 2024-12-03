package com.myapplication.database.repository

import android.util.Log
import com.myapplication.database.Signal
import com.myapplication.database.SignalDao
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("/api/stiprumai") // Replace with your actual API endpoint
    suspend fun getSignalStrengths(): Response<List<Signal>>
}

class SignalRepository(private val signalDao: SignalDao) {
    private val apiService: ApiService

    val allSignalStrengths: Flow<List<Signal>> = signalDao.getAllSignalStrengths()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000") // Replace with your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    // Function to refresh signals from the API and save them to the database
    suspend fun refreshSignalStrengths() {
        try {
            val response = apiService.getSignalStrengths() // Call the API
            if (response.isSuccessful) {
                val signals = response.body()
                if (signals != null) {
                    signalDao.clearOldMeasurements()
                    signalDao.resetIdCounter()
                    signalDao.insertAll(signals)
                    Log.d("SignalsRepository", "Fetched Signals")
                } else {
                    Log.e("SignalRepository", "No signals received from API")
                }
            } else {
                Log.e("SignalRepository", "API Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("SignalRepository", "Network Error: ${e.message}")
        }
    }
}