package com.waterdelivery.app.domain.model

data class Delivery(
    val id: String,
    val customerId: String,
    val date: Long,
    val deliveredQuantity: Int,
    val returnedQuantity: Int,
    val pricePerBottle: Double,
    val notes: String? = null,
    val createdAt: Long,
) {
    val totalAmount: Double
        get() = deliveredQuantity * pricePerBottle

    val netBottleBalance: Int
        get() = deliveredQuantity - returnedQuantity
}
