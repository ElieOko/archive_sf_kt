package com.example.archive_sfc.room.picture.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.Picture
import kotlinx.coroutines.flow.Flow


@Dao
interface IPictureDao{

    @Query("SELECT * FROM picture")
    fun getAll(): Flow<List<Picture>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(picture: Picture)
}

