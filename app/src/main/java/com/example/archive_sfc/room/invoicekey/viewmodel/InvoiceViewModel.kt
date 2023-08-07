package com.example.archive_sfc.room.invoicekey.viewmodel

import androidx.lifecycle.*
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.invoicekey.repository.InvoiceKeyRepository
import kotlinx.coroutines.launch

class InvoiceKeyViewModel (private val repository: InvoiceKeyRepository) : ViewModel()  {

    val allInvoiceKey: LiveData<List<InvoiceKey>> = repository.allInvoiceKey.asLiveData()

    val invoiceKey = MutableLiveData<InvoiceKey>()
    fun verify(_invoiceKeyId: Int) = repository.verify(_invoiceKeyId)
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(invoiceKey: InvoiceKey) = viewModelScope.launch {
        repository.insert(invoiceKey)
    }
}