package com.waterdelivery.app.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.waterdelivery.app.core.platform.ContactManager
import com.waterdelivery.app.core.platform.IosContactManager
import com.waterdelivery.app.core.platform.IosPdfInvoiceGenerator
import com.waterdelivery.app.core.platform.IosShareManager
import com.waterdelivery.app.core.platform.PdfInvoiceGenerator
import com.waterdelivery.app.core.platform.ShareManager
import com.waterdelivery.app.data.local.database.AppDatabase
import com.waterdelivery.app.data.local.database.AppDatabaseConstructor
import org.koin.dsl.module
import org.koin.core.module.Module
import platform.Foundation.NSHomeDirectory

actual val platformModule: Module = module {
    single<PdfInvoiceGenerator> { IosPdfInvoiceGenerator() }
    single<ShareManager> { IosShareManager() }
    single<ContactManager> { IosContactManager() }
    single<RoomDatabase.Builder<AppDatabase>> {
        val dbFilePath = NSHomeDirectory() + "/aqualog.db"
        Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
            factory = { AppDatabaseConstructor.initialize() }
        )
    }
}
