package com.example.archive_sfc.room.invoice.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.room.invoice.myinterface.IInvoiceDao
import kotlinx.coroutines.flow.Flow

class InvoiceRepository (private val invoiceDao: IInvoiceDao)  {
    val allInvoice: Flow<List<Invoice>> = invoiceDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(invoice: Invoice) {
        invoiceDao.insert(invoice)
    }
}