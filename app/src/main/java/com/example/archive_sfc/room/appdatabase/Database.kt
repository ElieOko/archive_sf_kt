package com.example.archive_sfc.room.appdatabase

import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.room.directory.myinterface.IDirectoryDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.archive_sfc.models.room.*
import com.example.archive_sfc.room.Converters
import com.example.archive_sfc.room.branch.myinterface.IBranchDao
import com.example.archive_sfc.room.invoice.myinterface.IInvoiceDao
import com.example.archive_sfc.room.invoicekey.myinterface.IInvoiceKeyDao
import com.example.archive_sfc.room.picture.myinterface.IPictureDao
import com.example.archive_sfc.room.user.myinterface.IUserDao
import com.example.archive_sfc.room.userdirectory.myinterface.IUserDirectoyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@TypeConverters(Converters::class)
@Database(entities = [User::class, Branch::class, Directory::class, UserDirectory::class, InvoiceKey::class,Invoice::class,Picture::class], version = 19, exportSchema = false)
public abstract class UserRoomDatabase : RoomDatabase() {

    abstract fun userDao(): IUserDao
    abstract fun branchDao():IBranchDao
    abstract fun directoryDao(): IDirectoryDao
    abstract fun userDirectoryDao(): IUserDirectoyDao
    abstract fun invoiceKeyDao(): IInvoiceKeyDao
    abstract fun invoiceDao(): IInvoiceDao
    abstract fun pictureDao(): IPictureDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null

        fun getDatabase(context: Context): UserRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserRoomDatabase::class.java,
                    "archive_sfc"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    private class UserDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.userDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: IUserDao) {
            // Delete all content here.
          //  wordDao.deleteAll()
            // Add sample words.
            var user = User(0,"elieoko100","0000")
            userDao.insert(user)
            // TODO: Add your own words!
        }
    }
}