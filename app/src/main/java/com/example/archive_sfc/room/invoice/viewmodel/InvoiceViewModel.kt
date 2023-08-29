package com.example.archive_sfc.room.invoice.viewmodel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.room.invoice.repository.InvoiceRepository
import kotlinx.coroutines.launch

class InvoiceViewModel (private val repository: InvoiceRepository) : ViewModel()  {

    val xs: LiveData<List<Invoice>> = repository.allInvoice.asLiveData()
    
    val allInvoice = repository.allInvoice.asLiveData()
    var allInvoices : MutableLiveData<List<Invoice>> = MutableLiveData<List<Invoice>>()
    fun update(invoice: Invoice) = viewModelScope.launch {
        repository.update(invoice)
    }

    fun getById(invoice: Invoice) = repository.getById(invoice.InvoiceId)
    fun insert(invoice: Invoice) = viewModelScope.launch {
        repository.insert(invoice)
    }
    fun deleteById(InvoiceId:Int) = viewModelScope.launch {
        repository.deleteById(InvoiceId)
    }

    fun getAllInvoice(): MutableLiveData<List<Invoice>> {
        viewModelScope.launch {
            allInvoice.observeForever {
                allInvoices.value = it
            }
        }
        return allInvoices
    }
}