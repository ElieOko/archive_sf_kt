package com.example.archive_sfc.room.branch.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.archive_sfc.room.url.repository.UrlRepository
import com.example.archive_sfc.room.url.viewModel.UrlViewModel

//class BranchViewModelFactory(private val repository: UrlRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(UrlViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return UrlViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}