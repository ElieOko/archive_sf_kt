package com.example.archive_sfc.room.url.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Branch
import com.example.archive_sfc.models.room.Url
import com.example.archive_sfc.room.branch.myinterface.IBranchDao
import com.example.archive_sfc.room.branch.myinterface.IUrlDao
import kotlinx.coroutines.flow.Flow

class UrlRepository (private val urlDao: IUrlDao)  {
    val allUrl : Flow<List<Url>> = urlDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(url: Url) {
        urlDao.insert(url)
    }
}