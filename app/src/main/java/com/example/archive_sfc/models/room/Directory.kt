package com.example.archive_sfc.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//(indices = [Index(value =["DirectoryId"], unique = true)])
@Entity
data class Directory(
    @PrimaryKey() val DirectoryId: Int,
    @ColumnInfo(name = "directoryName") val DirectoryName: String,
    @ColumnInfo(name = "parentId", defaultValue = "null") var parentId: Int? = null ,
    @ColumnInfo(name = "available", defaultValue = "1") val available: Int,
    @ColumnInfo(name = "created_at", defaultValue = "null") var created_at: String? = "" ,
    @ColumnInfo(name = "updated_at", defaultValue = "null") var updated_at: String? = "" ,
)
