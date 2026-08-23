package com.waterdelivery.app.data.local.mapper

import com.waterdelivery.app.data.local.entity.CustomerEntity
import com.waterdelivery.app.domain.model.Customer

fun CustomerEntity.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phoneNumber = phoneNumber,
    address = address,
    notes = notes,
    createdAt = createdAt,
    isActive = isActive,
)

fun Customer.toEntity(): CustomerEntity = CustomerEntity(
    id = id,
    name = name,
    phoneNumber = phoneNumber,
    address = address,
    notes = notes,
    createdAt = createdAt,
    isActive = isActive,
)
