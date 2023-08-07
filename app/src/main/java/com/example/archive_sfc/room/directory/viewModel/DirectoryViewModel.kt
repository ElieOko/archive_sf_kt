package com.example.archive_sfc.room.directory.viewModel

import com.example.archive_sfc.models.room.Directory
import androidx.lifecycle.*
import com.example.archive_sfc.room.directory.repository.DirectoryRepository
import kotlinx.coroutines.launch


class DirectoryViewModel (private val repository: DirectoryRepository) : ViewModel()  {

    val allDirectory: LiveData<List<Directory>> = repository.allDirectory.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val directory = MutableLiveData<Directory>()
    fun verify(_directory: String ="",parentId:Int =0) = repository.verify(_directory,parentId)
    fun insert(directory: Directory) = viewModelScope.launch {
        repository.insert(directory)
    }
}