package com.example.archive_sfc.room.user.repository

import androidx.annotation.WorkerThread
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.user.myinterface.IUserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: IUserDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allUser: Flow<List<User>> = userDao.getAll()

    fun getUser(user: User):User?{
       return userDao.getUser(user.username)
    }
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    fun auth(user: User) : User?{
       return userDao.auth(user.username,user.password)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        userDao.insert(user)
    }
}