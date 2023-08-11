package com.example.archive_sfc.room.picture.viewmodel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.picture.repository.PictureRepository
import kotlinx.coroutines.launch

class PictureViewModel (private val repository: PictureRepository) : ViewModel()  {

    val allPicture = MutableLiveData<List<Picture>>()
    val allImageByInvoice =  MutableLiveData<List<Picture>>()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(picture: Picture) = viewModelScope.launch {
        repository.insert(picture)
    }
    fun getAllImageByInvoice(invoiceFId:Int):MutableLiveData<List<Picture>>{
        viewModelScope.launch {
            repository.getAllById(invoiceFId).collect{
                allImageByInvoice.value = it
            }
        }
        return allImageByInvoice
    }
    /*
    [1 =>
    {},{},{}
    ]
     */
    fun getAllImages(): MutableLiveData<List<Picture>> {
        viewModelScope.launch {
            repository.allPicture.collect{
                allPicture.value = it
            }
        }
        return allPicture
    }
}