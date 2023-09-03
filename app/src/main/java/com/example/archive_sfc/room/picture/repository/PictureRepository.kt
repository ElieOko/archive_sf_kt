package com.example.archive_sfc.room.picture.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.picture.myinterface.IPictureDao
import kotlinx.coroutines.flow.Flow

class PictureRepository (private val pictureDao: IPictureDao)  {
    val allPicture: Flow<List<Picture>> = pictureDao.getAll()

    fun getAllById(invoiceId:Int) : Flow<List<Picture>> {
        return pictureDao.getByInvoiceId(invoiceId)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllFID(InvoiceFId:Int){
        pictureDao.deleteAllFID(InvoiceFId )
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        pictureDao.deleteAll()
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteGetById(PictureId:Int){
        pictureDao.deleteGetById(PictureId)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(picture: Picture) {
        pictureDao.insert(picture)
    }
}