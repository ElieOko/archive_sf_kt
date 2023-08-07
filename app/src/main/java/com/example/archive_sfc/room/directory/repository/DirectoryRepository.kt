package com.example.archive_sfc.room.directory.repository

import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.room.directory.myinterface.IDirectoryDao
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class DirectoryRepository (private val directoryDao: IDirectoryDao)  {
    val allDirectory: Flow<List<Directory>> = directoryDao.getAll()

    fun verify(directory: String ="",parentId:Int = 0) : Directory? {
        return directoryDao.verify(directory,parentId)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(directory: Directory) {
        directoryDao.insert(directory)
    }
}