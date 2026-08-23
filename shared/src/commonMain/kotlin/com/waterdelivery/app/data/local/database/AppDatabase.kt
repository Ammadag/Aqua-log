package com.waterdelivery.app.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.waterdelivery.app.data.local.dao.BusinessProfileDao
import com.waterdelivery.app.data.local.dao.CustomerDao
import com.waterdelivery.app.data.local.dao.DeliveryDao
import com.waterdelivery.app.data.local.dao.InvoiceDao
import com.waterdelivery.app.data.local.entity.BusinessProfileEntity
import com.waterdelivery.app.data.local.entity.CustomerEntity
import com.waterdelivery.app.data.local.entity.DeliveryEntity
import com.waterdelivery.app.data.local.entity.InvoiceEntity

@Database(
    entities = [
        CustomerEntity::class,
        DeliveryEntity::class,
        InvoiceEntity::class,
        BusinessProfileEntity::class,
    ],
    version = 1,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun businessProfileDao(): BusinessProfileDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
