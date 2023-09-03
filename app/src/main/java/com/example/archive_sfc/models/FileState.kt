package com.example.archive_sfc.models

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

data class FileState(
    val fileName :String,
    var bitmap: Bitmap,
    val uri: Uri?,
    val fileContent: File?
)


class DataPart (
    var fileName: String,
    var content: ByteArray,
    var type: String
)