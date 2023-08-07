package com.example.archive_sfc.room.invoicekey.myinterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.InvoiceKey
import kotlinx.coroutines.flow.Flow

@Dao
interface IInvoiceKeyDao {
    @Query("SELECT * FROM invoicekey")
    fun getAll(): Flow<List<InvoiceKey>>
    @Query("SELECT * FROM invoicekey WHERE invoiceKeyId LIKE :invoiceKeyId LIMIT 1")
    fun verify(invoiceKeyId: Int): InvoiceKey?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(invoiceKey: InvoiceKey)
}