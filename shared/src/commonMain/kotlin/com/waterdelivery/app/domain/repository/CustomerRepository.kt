package com.waterdelivery.app.domain.repository

import com.waterdelivery.app.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    suspend fun getCustomerById(id: String): Customer?
    fun searchCustomers(query: String): Flow<List<Customer>>
    suspend fun addCustomer(customer: Customer)
    suspend fun updateCustomer(customer: Customer)
}
