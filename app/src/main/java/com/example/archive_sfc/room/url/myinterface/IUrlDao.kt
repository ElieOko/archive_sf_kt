package com.example.archive_sfc.room.url.myinterface

import androidx.room.*
import com.example.archive_sfc.models.room.Url

import kotlinx.coroutines.flow.Flow

@Dao
interface IUrlDao {
    @Query("SELECT * FROM url")
    fun getAll(): Flow<List<Url>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(url: Url)
    @Update
    suspend fun update(url: Url)
}