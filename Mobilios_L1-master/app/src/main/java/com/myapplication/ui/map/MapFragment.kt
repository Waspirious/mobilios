package com.myapplication.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.myapplication.R
import com.myapplication.database.UserDatabase
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    private lateinit var gridView: MapView
    private lateinit var macroAddressSpinner: Spinner

    private val _closestCoordinatesLiveData = MutableLiveData<List<ClosestCoordinate>>()
    val closestCoordinatesLiveData: LiveData<List<ClosestCoordinate>> get() = _closestCoordinatesLiveData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        gridView = rootView.findViewById(R.id.gridView)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        macroAddressSpinner = rootView.findViewById(R.id.macroAddressSpinner)

        // Observe the LiveData for coordinate changes
        viewModel.coordinatesLiveData.observe(viewLifecycleOwner) { coordinates ->
            // Update the highlighted points in the grid view
            gridView.setHighlightedPoints(coordinates)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllMacAddresses().collect { macAddresses ->
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, macAddresses)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                macroAddressSpinner.adapter = adapter
            }
        }

        // Handle Spinner selection
        macroAddressSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMacAddress = parent.getItemAtPosition(position) as String
                redrawMapWithClosestPoints(selectedMacAddress)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        return rootView
    }

    private fun redrawMapWithClosestPoints(macAddress: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Retrieve the closest coordinates for the given macAddress
                val closestCoordinates = viewModel.findClosestCoordinatesForUser(macAddress)


                // Log the results
                if (closestCoordinates.isNotEmpty()) {
                    // Log and set the coordinates to the MapView
                    closestCoordinates.forEach { coordinate ->
                        Log.d(
                            "ClosestCoordinate",
                            "X: ${coordinate.xCoordinate}, Y: ${coordinate.yCoordinate}, Distance: ${coordinate.distance}"
                        )
                    }
                    // Update the MapView with the closest coordinates
                    gridView.setClosestCoordinates(closestCoordinates)
                } else {
                    Log.d(
                        "ClosestCoordinate",
                        "No matching coordinates found for MAC address: $macAddress"
                    )
                }
            } catch (e: Exception) {
                // Log any exception that occurs
                Log.e("ClosestCoordinate", "Error retrieving closest coordinates: ${e.message}", e)
            }
        }
    }
}
