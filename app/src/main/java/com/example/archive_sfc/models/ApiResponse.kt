package com.example.archive_sfc.models

data class ApiResponseDirectory(
    val directory :List<ApiDirectory>,
)

data class ApiResponseInvoiceKey(
    val invoiceKey:List<ApiInvoiceKey>
)
data class ApiDirectory(
    val DirectoryId :Int,
    val DirectoryName :String,
    val parentId :String?,
    val available:Int,
    val created_at:String,
    val updated_at:String
)
data class ApiInvoiceKey(
    val InvoicekeyId:Int,
    val Invoicekey:String,
    val DirectoryFId:Int,
    val created_at:String,
    val updated_at:String
)