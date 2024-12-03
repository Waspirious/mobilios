package com.myapplication.ui.measurements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.myapplication.database.Measurement
import com.myapplication.database.UserDatabase
import com.myapplication.database.repository.MeasurementRepository
import kotlinx.coroutines.launch

class MeasurementsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MeasurementRepository
    val allMeasurements: LiveData<List<Measurement>>

    init {
        val measurementDao = UserDatabase.getDatabase(application).measurementDao()
        repository = MeasurementRepository(measurementDao)
        allMeasurements = repository.getAllMeasurements().asLiveData()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshMeasurements()
        }
    }
}