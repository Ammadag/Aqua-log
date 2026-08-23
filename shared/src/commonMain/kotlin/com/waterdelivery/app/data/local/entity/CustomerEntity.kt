package com.waterdelivery.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val address: String,
    val notes: String?,
    val createdAt: Long,
    val isActive: Boolean,
)
