package com.example.archive_sfc.room.picture.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.picture.myinterface.IPictureDao
import kotlinx.coroutines.flow.Flow

class PictureRepository (private val pictureDao: IPictureDao)  {
    val allPicture: Flow<List<Picture>> = pictureDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(picture: Picture) {
        pictureDao.insert(picture)
    }
}