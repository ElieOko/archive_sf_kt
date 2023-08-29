package com.example.archive_sfc.room.invoice.myinterface

import androidx.room.*
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.User

import kotlinx.coroutines.flow.Flow


@Dao
interface IInvoiceDao {
    @Query("SELECT * FROM invoice")
    fun getAll(): Flow<List<Invoice>>
    @Query("SELECT * FROM invoice WHERE InvoiceId like :InvoiceId")
    fun getById(InvoiceId:Int): Invoice?
    @Update
    suspend fun update(invoice: Invoice)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(invoice: Invoice)

    @Query("DELETE FROM invoice")
    suspend fun deleteAll()
    @Query("DELETE FROM invoice WHERE InvoiceId like :InvoiceId")
    suspend fun deleteById(InvoiceId:Int)
}