package com.example.archive_sfc.room.branch.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.Url

import kotlinx.coroutines.flow.Flow

@Dao
interface IUrlDao {
    @Query("SELECT * FROM url")
    fun getAll(): Flow<List<Url>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(url: Url)
}