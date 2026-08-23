package com.waterdelivery.app.domain.model

data class Customer(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val address: String,
    val notes: String? = null,
    val createdAt: Long,
    val isActive: Boolean = true,
)
