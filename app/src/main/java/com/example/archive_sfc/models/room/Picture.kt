package com.example.archive_sfc.models.room

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true) val pictureId: Int,
    @ColumnInfo(name = "invoiceFId") val invoiceFId: Int,
    @ColumnInfo(name = "pictureName") val pictureName: String,
    @ColumnInfo(name = "picturePath") val picturePath: String,
    @ColumnInfo(name = "publicUrl", defaultValue = "") val PublicUrl: String,
    @ColumnInfo(name = "pictureOriginalName") var pictureOriginalName: Bitmap? = null,
)

