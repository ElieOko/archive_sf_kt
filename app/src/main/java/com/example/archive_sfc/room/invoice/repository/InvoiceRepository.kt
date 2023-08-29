package com.example.archive_sfc.room.invoice.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.room.invoice.myinterface.IInvoiceDao
import kotlinx.coroutines.flow.Flow

class InvoiceRepository (private val invoiceDao: IInvoiceDao)  {
    val allInvoice: Flow<List<Invoice>> = invoiceDao.getAll()

    fun getById(invoiceId: Int): Invoice?{
        return invoiceDao.getById(invoiceId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(invoice: Invoice) {
        invoiceDao.update(invoice)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(invoice: Invoice) {
        invoiceDao.insert(invoice)
    }
}