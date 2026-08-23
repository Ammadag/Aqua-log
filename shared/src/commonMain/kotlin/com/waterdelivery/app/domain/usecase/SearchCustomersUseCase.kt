package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow

class SearchCustomersUseCase(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(query: String): Flow<List<Customer>> {
        return customerRepository.searchCustomers(query)
    }
}
