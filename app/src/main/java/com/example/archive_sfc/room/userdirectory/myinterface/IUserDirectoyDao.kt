package com.example.archive_sfc.room.userdirectory.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.UserDirectory
import kotlinx.coroutines.flow.Flow

@Dao
interface IUserDirectoyDao{
    @Query("SELECT * FROM userdirectory")
    fun getAll(): Flow<List<UserDirectory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userDirectory: UserDirectory)
}