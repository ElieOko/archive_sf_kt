package com.example.archive_sfc.room.invoice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.room.invoice.repository.InvoiceRepository
import kotlinx.coroutines.launch

class InvoiceViewModel (private val repository: InvoiceRepository) : ViewModel()  {

    val allInvoice: LiveData<List<Invoice>> = repository.allInvoice.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(invoice: Invoice) = viewModelScope.launch {
        repository.insert(invoice)
    }
}