package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Status(
    @PrimaryKey() val id: Int,
    @ColumnInfo(name = "idUser") val idUser: Int
)
