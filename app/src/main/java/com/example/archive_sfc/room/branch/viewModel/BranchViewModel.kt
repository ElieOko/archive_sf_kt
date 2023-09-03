package com.example.archive_sfc.room.branch.viewModel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Branch
import com.example.archive_sfc.room.url.repository.UrlRepository
import kotlinx.coroutines.launch

//
//class BranchViewModel (private val repository: BranchViewModel) : ViewModel()  {
//
//    val allBranch: LiveData<List<Branch>> = repository.allBranch.asLiveData()
//    /**
//     * Launching a new coroutine to insert the data in a non-blocking way
//     */
//    fun insert(branch: Branch) = viewModelScope.launch {
//        repository.insert(branch)
//    }
//}