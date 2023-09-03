package com.example.archive_sfc.room.status.viewModel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Status
import com.example.archive_sfc.room.status.repository.StatusRepository
import kotlinx.coroutines.launch


class StatusViewModel (private val repository: StatusRepository) : ViewModel()  {

    val allStatus: LiveData<List<Status>> = repository.allStatus.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun deleteAll()= viewModelScope.launch {
        repository.deleteAll()
    }
    fun insert(status: Status) = viewModelScope.launch {
        repository.insert(status)
    }
    fun update(status: Status) = viewModelScope.launch {
        repository.update(status)
    }
}