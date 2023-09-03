package com.example.archive_sfc.room.status.myinterface

import androidx.room.*
import com.example.archive_sfc.models.room.Status

import kotlinx.coroutines.flow.Flow

@Dao
interface IStatusDao {
    @Query("SELECT * FROM status")
    fun getAll(): Flow<List<Status>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(status: Status)

    @Update
    suspend fun update(status: Status)
    @Query("DELETE FROM status")
    suspend fun deleteAll()
}