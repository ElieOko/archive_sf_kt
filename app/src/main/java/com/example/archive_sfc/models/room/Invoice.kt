package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value =["invoiceUniqueId"], unique = true)])
data class Invoice(
    @PrimaryKey(autoGenerate = true) val InvoiceId: Int,
    @ColumnInfo(name = "invoiceCode") val invoiceCode: String,
    @ColumnInfo(name = "invoiceDesc") val invoiceDesc: String,
    @ColumnInfo(name = "invoiceBarCode") val invoiceBarCode: String,
    @ColumnInfo(name = "userFId") val UserFId: Int,
    @ColumnInfo(name = "directoryFId") val DirectoryFId: Int,
    @ColumnInfo(name = "branchFId") val BranchFId: Int,
    @ColumnInfo(name = "invoiceDate") val invoiceDate: String,
    @ColumnInfo(name = "invoicekeyFId") val InvoicekeyFId: Int,
    @ColumnInfo(name = "invoicePath") val invoicePath: String,
    @ColumnInfo(name = "androidVersion") val androidVersion: String,
    @ColumnInfo(name = "invoiceUniqueId") val invoiceUniqueId: String,
    @ColumnInfo(name = "clientName", defaultValue = "null") val clientName: String? = "" ,
    @ColumnInfo(name = "clientPhone", defaultValue = "null") val clientPhone: String? = "" ,
    @ColumnInfo(name = "expiredDate", defaultValue = "null") val expiredDate: String? = "" ,
)
/*
            $table->id("InvoiceId");
            $table->string("InvoiceCode")->nullable();
            $table->string("InvoiceDesc")->nullable();
            $table->string("InvoiceBarCode")->nullable();
            $table->unsignedBigInteger('UserFId');
            $table->foreign('UserFId')->references('UserId')->on('users');
            $table->unsignedBigInteger('DirectoryFId');
            $table->foreign('DirectoryFId')->references('DirectoryId')->on('tdirectories');
            $table->unsignedBigInteger('BranchFId');
            $table->foreign('BranchFId')->references('BranchId')->on('t_branches');
            $table->timestamps();
            $table->date("InvoiceDate");
            $table->unsignedBigInteger('InvoicekeyFId');
            $table->foreign('InvoicekeyFId')->references('InvoicekeyId')->on('tinvoicekeys');
            $table->string("InvoicePath")->nullable();
            $table->string("AndroidVersion")->nullable();
            $table->string("InvoiceUniqueId")->nullable();
            $table->string("ClientName")->nullable();
            $table->string("ClientPhone")->nullable();
            $table->date("ExpiredDate")->nullable();
 */