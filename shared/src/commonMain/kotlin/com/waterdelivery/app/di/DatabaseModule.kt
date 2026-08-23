package com.waterdelivery.app.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.waterdelivery.app.data.local.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        val builder = get<RoomDatabase.Builder<AppDatabase>>()
        builder
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<AppDatabase>().customerDao() }
    single { get<AppDatabase>().deliveryDao() }
    single { get<AppDatabase>().invoiceDao() }
    single { get<AppDatabase>().businessProfileDao() }
}
