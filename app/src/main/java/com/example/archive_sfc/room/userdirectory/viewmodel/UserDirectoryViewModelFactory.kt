package com.example.archive_sfc.room.userdirectory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.archive_sfc.room.userdirectory.repository.UserDirectoryRepository

class UserDirectoryViewModelFactory(private val repository: UserDirectoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDirectoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDirectoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}