package com.waterdelivery.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.waterdelivery.app.core.platform.AndroidContactManager
import com.waterdelivery.app.core.platform.AndroidPdfInvoiceGenerator
import com.waterdelivery.app.core.platform.AndroidShareManager
import com.waterdelivery.app.core.platform.ContactManager
import com.waterdelivery.app.core.platform.PdfInvoiceGenerator
import com.waterdelivery.app.core.platform.ShareManager
import com.waterdelivery.app.data.local.database.AppDatabase
import org.koin.dsl.module
import org.koin.core.module.Module

actual val platformModule: Module = module {
    single<PdfInvoiceGenerator> { AndroidPdfInvoiceGenerator(get()) }
    single<ShareManager> { AndroidShareManager(get()) }
    single<ContactManager> { AndroidContactManager(get()) }
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = get<Context>()
        val dbFile = context.getDatabasePath("aqualog.db")
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
    }
}
