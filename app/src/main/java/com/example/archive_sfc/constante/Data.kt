package com.example.archive_sfc.constante

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.models.room.User

object Data {
    var user : User? = null
    val allInvoicePicture:ArrayList<List<Picture>> = ArrayList()
    var token:String = ""
    var idUser :Int =1
}

