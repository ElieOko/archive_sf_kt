package com.example.archive_sfc.room.status.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Status
import com.example.archive_sfc.room.status.myinterface.IStatusDao
import kotlinx.coroutines.flow.Flow

class StatusRepository (private val statusDao: IStatusDao)  {
    val allStatus: Flow<List<Status>> = statusDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() = statusDao.deleteAll()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(status: Status) {
        statusDao.update(status)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(status: Status) {
        statusDao.insert(status)
    }
}