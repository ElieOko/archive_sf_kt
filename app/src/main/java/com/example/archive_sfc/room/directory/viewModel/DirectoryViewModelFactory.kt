package com.example.archive_sfc.room.directory.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.archive_sfc.room.directory.repository.DirectoryRepository

class DirectoryViewModelFactory(private val repository: DirectoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}