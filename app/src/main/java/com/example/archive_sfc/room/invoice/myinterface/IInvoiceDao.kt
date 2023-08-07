package com.example.archive_sfc.room.invoice.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.Invoice

import kotlinx.coroutines.flow.Flow


@Dao
interface IInvoiceDao {
    @Query("SELECT * FROM invoice")
    fun getAll(): Flow<List<Invoice>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(invoice: Invoice)
}