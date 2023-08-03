package com.example.archive_sfc.myinterface

import androidx.room.*
import com.example.archive_sfc.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface IUserDao {
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

//    @Query("SELECT * FROM user WHERE id IN (:userIds)")
//    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE username LIKE :username AND " +
            "password LIKE :password LIMIT 1")
    fun auth(username: String, password: String): User?
//    @Delete
//    fun delete(user: User)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(users: User)
//
//    @Query("DELETE FROM user")
//    suspend fun deleteAll()
}