package com.example.archive_sfc.room.user.viewmodel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.user.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.




//    private val _getNetworkDataStatus = MutableLiveData<Resource<NetworkResponse>>()
//
//    val getNetworkDataStatus: MutableLiveData<Resource<NetworkResponse>> = _getNetworkDataStatus
//
//
//
    val allWords: LiveData<List<User>> = repository.allUser.asLiveData()
    val user = MutableLiveData<User>()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(user: User) = viewModelScope.launch {

        repository.insert(user)
    }

    fun getUser(_user: User) = repository.getUser(_user)
    suspend fun auth(_user: User) :User?=
        withContext(viewModelScope.coroutineContext) {
            repository.auth(_user)
        }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}