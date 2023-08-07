package com.example.archive_sfc

import android.app.Application
import com.example.archive_sfc.room.appdatabase.UserRoomDatabase
import com.example.archive_sfc.room.branch.repository.BranchRepository
import com.example.archive_sfc.room.directory.repository.DirectoryRepository
import com.example.archive_sfc.room.invoice.repository.InvoiceRepository
import com.example.archive_sfc.room.invoicekey.repository.InvoiceKeyRepository
import com.example.archive_sfc.room.picture.repository.PictureRepository
import com.example.archive_sfc.room.user.repository.UserRepository
import com.example.archive_sfc.room.userdirectory.repository.UserDirectoryRepository

class UserApplication : Application() {

        val database by lazy { UserRoomDatabase.getDatabase(this) }
        val repository by lazy { UserRepository(database.userDao()) }
        val repositoryBranch by lazy { BranchRepository(database.branchDao()) }
        val repositoryDirectory by lazy {DirectoryRepository(database.directoryDao())}
        val repositoryUserDirectory by lazy { UserDirectoryRepository(database.userDirectoryDao()) }
        val repositoryInvoiceKey by lazy { InvoiceKeyRepository(database.invoiceKeyDao()) }
        val repositoryInvoice by lazy { InvoiceRepository(database.invoiceDao()) }
        val repositoryPicture by lazy { PictureRepository(database.pictureDao()) }

}