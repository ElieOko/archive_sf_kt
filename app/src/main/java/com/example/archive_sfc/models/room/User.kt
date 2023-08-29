package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey() val UserId: Int =0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "email") val email: String?="",
    @ColumnInfo(name = "role") val role: Int=0,
    @ColumnInfo(name = "serialNumber") var serialNumber: String? ="",
    @ColumnInfo(name = "phone") var phone: String? = "",
    @ColumnInfo(name = "branchFId") var BranchFId: Int? = 0,
    @ColumnInfo(name = "email_verified_at", defaultValue = "null") var email_verified_at: String? = "",
    @ColumnInfo(name = "smstoken", defaultValue = "null") var smstoken: String? = "",
    @ColumnInfo(name = "created_at", defaultValue = "null") var created_at: String? = "",
    @ColumnInfo(name = "updated_at", defaultValue = "null") var updated_at: String? = "",
)



