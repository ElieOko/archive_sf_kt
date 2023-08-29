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
    @Query("SELECT * FROM picture WHERE InvoiceFId LIKE :InvoiceFId")
    fun getByInvoiceId(InvoiceFId:Int): Flow<List<Picture>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(picture: Picture)

    @Query("DELETE FROM picture")
    suspend fun deleteAll()

    @Query("DELETE FROM picture WHERE InvoiceFId LIKE :InvoiceFId")
    suspend fun deleteGetById(InvoiceFId:Int)
}

