package com.example.archive_sfc.room

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun compressionImage(imageCompress:ByteArray): ByteArray {
    var compressImg = imageCompress
    while (compressImg.size > 500_000){
        val bitmap = BitmapFactory.decodeByteArray(compressImg,0,compressImg.size)
        val resized =Bitmap.createScaledBitmap(bitmap,(bitmap.width * 0.8).toInt(),(bitmap.height * 0.8).toInt(),true)
        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.PNG,100, stream)
        compressImg = stream.toByteArray()
    }
    return compressImg
}