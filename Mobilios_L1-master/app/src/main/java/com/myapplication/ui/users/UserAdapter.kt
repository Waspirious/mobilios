package com.myapplication.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.database.User
import com.myapplication.databinding.UserItemBinding

class UserAdapter(private val editClickListener: OnEditClickListener) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users = emptyList<User>()

    inner class UserViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.macAddress.text = user.macAddress
            binding.signalStrength1.text = String.format("%d", user.signalStrength1)
            binding.signalStrength2.text = String.format("%d", user.signalStrength2)
            binding.signalStrength3.text = String.format("%d", user.signalStrength3)

            // Set up the edit button click listener
            binding.editButton.setOnClickListener {
                editClickListener.onEditClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun setUsers(users: List<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id  // Compare based on unique id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem  // Compare content
        }
    }

    interface OnEditClickListener {
        fun onEditClick(user: User)
    }
}
