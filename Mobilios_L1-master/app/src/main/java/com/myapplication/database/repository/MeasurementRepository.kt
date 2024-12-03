package com.myapplication.database.repository

import android.util.Log
import com.myapplication.database.Measurement
import com.myapplication.database.MeasurementDao
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MeasurementApiService {
    @GET("/api/matavimai")
    suspend fun getMeasurements(): Response<List<Measurement>>
}

class MeasurementRepository(private val measurementDao: MeasurementDao) {
    private val apiService: MeasurementApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000") // Replace with your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(MeasurementApiService::class.java)
    }

    // Function to refresh measurements from the API and save them to the database
    suspend fun refreshMeasurements() {
        try {
            val response = apiService.getMeasurements()
            if (response.isSuccessful) {
                val measurements = response.body()
                if (measurements != null) {
                    measurementDao.clearOldMeasurements()
                    measurementDao.resetIdCounter()
                    measurementDao.insertAll(measurements)
                    Log.d("MeasurementRepository", "Fetched measurements")
                } else {
                    Log.e("MeasurementRepository", "No measurements received from API")
                }
            } else {
                Log.e("MeasurementRepository", "API Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("MeasurementRepository", "Network Error: ${e.message}")
        }
    }

    fun getAllMeasurements(): Flow<List<Measurement>> = measurementDao.getAllMeasurements()
}
