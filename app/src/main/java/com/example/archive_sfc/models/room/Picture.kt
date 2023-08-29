package com.example.archive_sfc.models.room

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true) val PictureId: Int,
    @ColumnInfo(name = "InvoiceFId") val InvoiceFId: Int,
    @ColumnInfo(name = "pictureName") val pictureName: String,
    @ColumnInfo(name = "picturePath") val picturePath: String,
    @ColumnInfo(name = "publicUrl", defaultValue = "") val PublicUrl: String,
    @ColumnInfo(name = "contentFile") var contentFile: Bitmap? = null,
    @ColumnInfo(name = "isEnabled",defaultValue = "false") var isEnabled: Boolean = false,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, defaultValue = "test") var image: ByteArray? = null
//    @ColumnInfo(name = "pictureOriginalName") var pictureOriginalName: Bitmap? = null,
)

