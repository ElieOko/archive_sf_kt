package com.example.archive_sfc.room.url.viewModel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Branch
import com.example.archive_sfc.models.room.Url
import com.example.archive_sfc.room.url.repository.UrlRepository
import kotlinx.coroutines.launch


class UrlViewModel (private val repository: UrlRepository) : ViewModel()  {

    val allUrl: LiveData<List<Url>> = repository.allUrl.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(url: Url) = viewModelScope.launch {
        repository.insert(url)
    }
}