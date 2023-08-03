package com.example.archive_sfc.models

import android.net.Uri
import java.io.File

data class FileState(
    val fileName :String,
    val fileContent: File?,
    val uri: Uri?
)
