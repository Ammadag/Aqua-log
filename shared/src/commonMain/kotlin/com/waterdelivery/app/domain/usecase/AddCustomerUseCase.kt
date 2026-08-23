package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.repository.CustomerRepository
import kotlin.time.Clock

class AddCustomerUseCase(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(name: String, phone: String, address: String, notes: String? = null): Result<Unit> {
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Customer name cannot be empty"))
        if (phone.isBlank()) return Result.failure(IllegalArgumentException("Phone number cannot be empty"))

        val now = Clock.System.now().toEpochMilliseconds()
        val customer = Customer(
            id = now.toString(), // Simple ID generation
            name = name.trim(),
            phoneNumber = phone.trim(),
            address = address.trim(),
            notes = notes,
            createdAt = now,
            isActive = true
        )

        return try {
            customerRepository.addCustomer(customer)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
