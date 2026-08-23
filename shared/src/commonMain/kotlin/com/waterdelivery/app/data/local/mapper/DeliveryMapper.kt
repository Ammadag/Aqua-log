package com.waterdelivery.app.data.local.mapper

import com.waterdelivery.app.data.local.entity.DeliveryEntity
import com.waterdelivery.app.domain.model.Delivery

fun DeliveryEntity.toDomain(): Delivery = Delivery(
    id = id,
    customerId = customerId,
    date = date,
    deliveredQuantity = deliveredQuantity,
    returnedQuantity = returnedQuantity,
    pricePerBottle = pricePerBottle,
    notes = notes,
    createdAt = createdAt,
)

fun Delivery.toEntity(): DeliveryEntity = DeliveryEntity(
    id = id,
    customerId = customerId,
    date = date,
    deliveredQuantity = deliveredQuantity,
    returnedQuantity = returnedQuantity,
    pricePerBottle = pricePerBottle,
    totalAmount = totalAmount,
    notes = notes,
    createdAt = createdAt,
)
