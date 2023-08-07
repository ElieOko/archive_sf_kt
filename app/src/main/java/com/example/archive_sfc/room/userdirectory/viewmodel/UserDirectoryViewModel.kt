package com.example.archive_sfc.room.userdirectory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.archive_sfc.models.room.UserDirectory
import com.example.archive_sfc.room.userdirectory.repository.UserDirectoryRepository
import kotlinx.coroutines.launch

class UserDirectoryViewModel (private val repository: UserDirectoryRepository) : ViewModel()  {

    val allDirectory: LiveData<List<UserDirectory>> = repository.allDirectory.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(userDirectory: UserDirectory) = viewModelScope.launch {
        repository.insert(userDirectory)
    }
}