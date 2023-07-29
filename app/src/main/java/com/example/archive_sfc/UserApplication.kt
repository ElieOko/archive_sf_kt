package com.example.archive_sfc

import android.app.Application
import com.example.archive_sfc.appdatabase.UserRoomDatabase
import com.example.archive_sfc.repository.UserRepository

class UserApplication : Application() {

        val database by lazy { UserRoomDatabase.getDatabase(this) }
        val repository by lazy { UserRepository(database.userDao()) }


}