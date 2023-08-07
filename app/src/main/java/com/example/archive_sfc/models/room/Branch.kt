package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Branch(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "branchId") val branchId: Int,
    @ColumnInfo(name = "branchName") val branchName: String,
)
