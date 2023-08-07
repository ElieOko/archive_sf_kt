package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class InvoiceKey(
    @PrimaryKey() val InvoicekeyId: Int,
    @ColumnInfo(name = "invoiceKey") val Invoicekey: String,
    @ColumnInfo(name = "directoryFId") val DirectoryFId: Int,
    @ColumnInfo(name = "created_at", defaultValue = "null") var created_at: String? = "" ,
    @ColumnInfo(name = "updated_at", defaultValue = "null") var updated_at: String? = ""
)

