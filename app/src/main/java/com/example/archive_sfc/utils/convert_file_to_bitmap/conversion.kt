package com.example.archive_sfc.utils.convert_file_to_bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun convertFileToBitmap(file: File?): Bitmap {
    val path = file?.path
    return BitmapFactory.decodeFile(path)
}