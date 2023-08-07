package com.example.archive_sfc.room.userdirectory.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.UserDirectory
import com.example.archive_sfc.room.userdirectory.myinterface.IUserDirectoyDao
import kotlinx.coroutines.flow.Flow

class UserDirectoryRepository (private val userDirectoryDao: IUserDirectoyDao){
    val allDirectory: Flow<List<UserDirectory>> = userDirectoryDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userDirectory: UserDirectory) {
        userDirectoryDao.insert(userDirectory)
    }
}