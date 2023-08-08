package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserDirectory(
    @PrimaryKey(autoGenerate = true) val UserDirectoryId: Int,
    @ColumnInfo(name = "userFId") val UserFId: Int,
    @ColumnInfo(name = "directoryFId") val DirectoryFId: Int,
)
