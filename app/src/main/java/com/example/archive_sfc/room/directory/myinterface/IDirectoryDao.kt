package com.example.archive_sfc.room.directory.myinterface

import com.example.archive_sfc.models.room.Directory
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.User
import kotlinx.coroutines.flow.Flow

@Dao
interface IDirectoryDao {
    @Query("SELECT * FROM directory")
    fun getAll(): Flow<List<Directory>>

    @Query("SELECT * FROM directory WHERE directoryName LIKE :directoryName OR " +
            "DirectoryId LIKE :parentId LIMIT 1")
    fun verify(directoryName: String ="",parentId:Int = 0): Directory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(directory: Directory)
}