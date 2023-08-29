package com.example.archive_sfc.models

import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.Picture

data class InvoicePicture(
    val list:ArrayList<Invoice>,
    val listPicture:ArrayList<List<Picture>>
)
