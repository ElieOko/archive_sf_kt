package com.example.archive_sfc.room.branch.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.Branch

import kotlinx.coroutines.flow.Flow

@Dao
interface IBranchDao {
    @Query("SELECT * FROM branch")
    fun getAll(): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(branch: Branch)
}