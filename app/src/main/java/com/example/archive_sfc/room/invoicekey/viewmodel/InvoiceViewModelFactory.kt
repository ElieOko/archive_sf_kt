package com.example.archive_sfc.room.invoicekey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.archive_sfc.room.invoicekey.repository.InvoiceKeyRepository

class InvoiceKeyViewModelFactory(private val repository: InvoiceKeyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoiceKeyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvoiceKeyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}