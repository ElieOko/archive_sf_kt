package com.example.archive_sfc.room.branch.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Branch
import com.example.archive_sfc.room.branch.myinterface.IBranchDao
import kotlinx.coroutines.flow.Flow

class BranchRepository (private val branchDao: IBranchDao)  {
    val allBranch: Flow<List<Branch>> = branchDao.getAll()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(branch: Branch) {
        branchDao.insert(branch)
    }
}