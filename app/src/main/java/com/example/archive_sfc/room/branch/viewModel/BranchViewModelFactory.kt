package com.example.archive_sfc.room.branch.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.archive_sfc.room.branch.repository.BranchRepository

class BranchViewModelFactory(private val repository: BranchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BranchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}