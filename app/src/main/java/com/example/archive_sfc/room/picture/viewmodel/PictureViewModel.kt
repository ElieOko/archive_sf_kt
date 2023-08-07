package com.example.archive_sfc.room.picture.viewmodel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.picture.repository.PictureRepository
import kotlinx.coroutines.launch

class PictureViewModel (private val repository: PictureRepository) : ViewModel()  {

    val allPicture: LiveData<List<Picture>> = repository.allPicture.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(picture: Picture) = viewModelScope.launch {
        repository.insert(picture)
    }
}