package com.example.archive_sfc.room.invoicekey.repository


import com.example.archive_sfc.room.invoicekey.myinterface.IInvoiceKeyDao
import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.models.room.User
import kotlinx.coroutines.flow.Flow

class InvoiceKeyRepository (private val invoiceKeyDao: IInvoiceKeyDao)  {
    val allInvoiceKey: Flow<List<InvoiceKey>> = invoiceKeyDao.getAll()
    fun verify(invoiceKey: Int) : InvoiceKey? {
        return invoiceKeyDao.verify(invoiceKey)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(invoiceKey: InvoiceKey) {
        invoiceKeyDao.insert(invoiceKey)
    }
}