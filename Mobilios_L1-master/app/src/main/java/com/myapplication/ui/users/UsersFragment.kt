package com.myapplication.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapplication.R
import com.myapplication.database.User
import com.myapplication.database.UserDatabase
import com.myapplication.database.repository.UserRepository
import com.myapplication.databinding.FragmentUsersBinding
import com.myapplication.ui.measurements.MeasurementsViewModel

class UsersFragment : Fragment(), UserAdapter.OnEditClickListener {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: UserRepository
    private lateinit var usersViewModel: UsersViewModel
    private val measurementsViewModel: MeasurementsViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        val userDao = UserDatabase.getDatabase(requireContext()).userDao()
        repository = UserRepository(userDao)

        // Initialize the ViewModel after the repository has been set
        usersViewModel = ViewModelProvider(this, UsersViewModelFactory(repository)).get(UsersViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter(this) // Pass this as the OnEditClickListener
        binding.recyclerViewUser.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        // Observe the users data
        usersViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            users?.let { userAdapter.setUsers(it) }
        }

        binding.fabAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun showAddUserDialog() {
        // Create an AlertDialog to get user input
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_user, null)

        val macAddressEditText = dialogView.findViewById<EditText>(R.id.editTextMacAddress)
        val signalStrength1EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength1)
        val signalStrength2EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength2)
        val signalStrength3EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength3)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add User")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val macAddress = macAddressEditText.text.toString()
                val signalStrength1 = signalStrength1EditText.text.toString().toIntOrNull() ?: 0
                val signalStrength2 = signalStrength2EditText.text.toString().toIntOrNull() ?: 0
                val signalStrength3 = signalStrength3EditText.text.toString().toIntOrNull() ?: 0

                if (macAddress.isNotBlank()) {
                    val newUser = User(
                        macAddress = macAddress,
                        signalStrength1 = signalStrength1,
                        signalStrength2 = signalStrength2,
                        signalStrength3 = signalStrength3
                    )
                    usersViewModel.insert(newUser)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.create().show()
    }

    private fun showEditUserDialog(user: User) {
        // Create an AlertDialog to edit user input
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)

        val macAddressEditText = dialogView.findViewById<EditText>(R.id.editTextMacAddress)
        val signalStrength1EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength1)
        val signalStrength2EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength2)
        val signalStrength3EditText = dialogView.findViewById<EditText>(R.id.editTextSignalStrength3)

        // Pre-fill the EditText fields with existing user data
        macAddressEditText.setText(user.macAddress)
        signalStrength1EditText.setText(user.signalStrength1.toString())
        signalStrength2EditText.setText(user.signalStrength2.toString())
        signalStrength3EditText.setText(user.signalStrength3.toString())

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedMacAddress = macAddressEditText.text.toString()
                val updatedSignalStrength1 = signalStrength1EditText.text.toString().toIntOrNull() ?: 0
                val updatedSignalStrength2 = signalStrength2EditText.text.toString().toIntOrNull() ?: 0
                val updatedSignalStrength3 = signalStrength3EditText.text.toString().toIntOrNull() ?: 0

                if (updatedMacAddress.isNotBlank()) {
                    val updatedUser = user.copy(
                        macAddress = updatedMacAddress,
                        signalStrength1 = updatedSignalStrength1,
                        signalStrength2 = updatedSignalStrength2,
                        signalStrength3 = updatedSignalStrength3
                    )
                    usersViewModel.update(updatedUser)  // Assume update method is implemented in ViewModel
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.create().show()
    }

    override fun onEditClick(user: User) {
        showEditUserDialog(user) // Call the method to show the edit dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
